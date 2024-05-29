package com.example.capstonecodinavi.Camera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.SurfaceTexture
import android.graphics.YuvImage
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.capstonecodinavi.R
import com.example.capstonecodinavi.databinding.FragmentCameraBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment() {

    private lateinit var binding: FragmentCameraBinding
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private lateinit var overlayView: OverlayView
    private lateinit var objectDetectorHelper: ObjectDetectorHelper
    private var isOverlayViewReady = false
    private var pendingImageProxy: ImageProxy? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        overlayView = view.findViewById(R.id.overlayView)
        objectDetectorHelper = ObjectDetectorHelper(requireContext())

        overlayView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                isOverlayViewReady = true
                objectDetectorHelper.setOverlayViewSize(overlayView.width.toFloat(), overlayView.height.toFloat())
                pendingImageProxy?.let {
                    processImageProxy(it)
                    pendingImageProxy = null
                }
            }

            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
                isOverlayViewReady = true
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                isOverlayViewReady = false
                return true
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
                // Do nothing
            }
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
        startCamera()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageCapture = ImageCapture.Builder().build() // ImageCapture 초기화

            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                if (isOverlayViewReady) {
                    processImageProxy(imageProxy)
                } else {
                    pendingImageProxy = imageProxy
                }
            }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis, imageCapture)
            } catch (exc: Exception) {
                Log.e("CameraFragment", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun processImageProxy(imageProxy: ImageProxy) {
        Log.d("CameraFragment", "Processing image proxy")
        val bitmap = imageProxyToBitmap(imageProxy)
        val detectionResults = objectDetectorHelper.detect(bitmap)

        activity?.runOnUiThread {
            if (overlayView.isAvailable) {
                Log.d("CameraFragment", "OverlayView surface is available")
                overlayView.setResults(detectionResults)
                updateTextView(detectionResults)
            } else {
                Log.e("CameraFragment", "OverlayView surface is not available, retrying in 100ms")
                overlayView.postDelayed({
                    if (overlayView.isAvailable) {
                        overlayView.setResults(detectionResults)
                        updateTextView(detectionResults)
                    } else {
                        Log.e("CameraFragment", "OverlayView surface is still not available")
                    }
                }, 100)
            }
        }

        imageProxy.close()
    }

    private fun updateTextView(detectionResults: List<DetectionResult>) {
        val message: String
        if (detectionResults.isNotEmpty()) {
            val boundingBox = detectionResults[0].boundingBox
            val boxWidth = boundingBox.width()
            val boxHeight = boundingBox.height()

            val minSize = 400
            val maxSize = 800

            message = when {
                boxWidth < minSize || boxHeight < minSize -> {
                    "가까이 가주세요."
                }
                boxWidth > maxSize || boxHeight > maxSize -> {
                    "떨어져 주세요."
                }
                else -> {
                    "사진을 찍어주세요."
                }
            }
        } else {
            message = "다시 인식해주세요."
        }
        (activity as? CameraActivity)?.updateTextView(message)
    }

    private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap {
        val yBuffer = imageProxy.planes[0].buffer
        val uBuffer = imageProxy.planes[1].buffer
        val vBuffer = imageProxy.planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)

        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, imageProxy.width, imageProxy.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, imageProxy.width, imageProxy.height), 100, out)
        val imageBytes = out.toByteArray()

        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        return Bitmap.createScaledBitmap(bitmap, 416, 416, true)
    }
    fun uploadImage(file: File) {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val uniqueFileName = "IMG_${timeStamp}.jpg"

        val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
        val body = MultipartBody.Part.createFormData("file", uniqueFileName, requestFile)

        val call = RetrofitClient.instance.uploadImage(body)
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    Log.d("CameraFragment", "Image upload successful: ${response.body()}")
                    Toast.makeText(context, "Image upload successful", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("CameraFragment", "Image upload failed: ${response.errorBody()?.string()}")
                    Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("CameraFragment", "Image upload error: ${t.message}")
                Toast.makeText(context, "Image upload error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }


    fun getImageCapture(): ImageCapture? {
        return imageCapture
    }
}
