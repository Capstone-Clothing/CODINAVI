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
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
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
        val bitmap = imageProxyToBitmap(imageProxy)
        val detectionResults = objectDetectorHelper.detect(bitmap)

        activity?.runOnUiThread {
            if (overlayView.isAvailable) {
                overlayView.setResults(detectionResults)
                updateTextView(detectionResults)
            } else {
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
        val recogmessage: String
        if (detectionResults.isNotEmpty()) {
            val boundingBox = detectionResults[0].boundingBox
            val boxWidth = boundingBox.width()
            val boxHeight = boundingBox.height()

            val minSize = 400
            val maxSize = 800

            recogmessage = when {
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
            recogmessage = "다시 인식해주세요."
        }
        (activity as? CameraActivity)?.updateTextView(recogmessage)
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
        val uniqueId = UUID.randomUUID().toString()
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val uniqueFileName = "${uniqueId}_IMG_${timeStamp}.jpg"
        Log.d("CameraFragment", "Generated unique file name: $uniqueFileName")

        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("Imagefile", uniqueFileName, requestFile)

        val apiService = RetrofitClient.instance
        val call = apiService.uploadImage(body)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string() ?: uniqueFileName
                    val storedFileName = responseBody.substringAfter("이미지 업로드 성공: ").trim()
                    Log.d("CameraFragment", "Stored file name: $storedFileName")
                    Toast.makeText(context, "Image upload successful", Toast.LENGTH_SHORT).show()
                    getAnalysisResult(storedFileName)

                } else {
                    Log.e("CameraFragment", "Image upload failed: ${response.errorBody()?.string()}")
                    Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("CameraFragment", "Image upload error: ${t.message}")
                Toast.makeText(context, "Image upload error", Toast.LENGTH_SHORT).show()
            }
        })
        getAnalysisResult(uniqueFileName)
    }

    private fun getAnalysisResult(imageId: String) {
        Log.d("CameraFragment", "Requesting analysis for image: $imageId")
        val request = AnalysisRequest(
            codinaviImage = "codinavi-image",
            key = imageId
        )

        val call = RetrofitFlaskClient.instance.getAnalysisResult(request)
        call.enqueue(object : Callback<AnalysisResult> {
            override fun onResponse(call: Call<AnalysisResult>, response: Response<AnalysisResult>) {
                Log.d("CameraFragment", "Response received: ${response.body()}")
                if (response.isSuccessful) {
                    val result = response.body()
                    result?.let {
                        val message2 = "Pattern: ${it.pattern}, Type: ${it.type}, Dominant Color: ${it.dominant_color}"
                        (activity as CameraActivity).updateAnalysisResult(message2)
                    }
                } else {
                    Log.e("CameraFragment", "Failed to get analysis result: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<AnalysisResult>, t: Throwable) {
                Log.e("CameraFragment", "Error: ${t.message}")
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
