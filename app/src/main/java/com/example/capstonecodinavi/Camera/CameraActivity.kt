package com.example.capstonecodinavi.Camera

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.capstonecodinavi.Recommend.CodiRecommendActivity
import com.example.capstonecodinavi.Recommend.ColorRecommendActivity
import com.example.capstonecodinavi.Main.MainActivity
import com.example.capstonecodinavi.User.UserActivity
import com.example.capstonecodinavi.databinding.ActivityCameraBinding
import java.io.File
import java.util.concurrent.ExecutorService

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private lateinit var photoFile: File
    private lateinit var objectDetectorHelper: ObjectDetectorHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        action()

    }

    private fun action() {
        binding.homeBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.profileBtn.setOnClickListener {
            val intent = Intent(this, UserActivity::class.java)
            startActivity(intent)
        }

        binding.captureBtn.setOnClickListener {
            val navFragment: NavHostFragment = binding.fragmentContainer.getFragment()
            val cameraFragment: CameraFragment = navFragment.childFragmentManager.fragments[0] as CameraFragment
            imageCapture = cameraFragment.imageCapture
            photoFile = File(
                applicationContext.cacheDir,
                "newImage.jpg"
            )
            takePhoto()
            Log.d("check test", "$imageCapture")
        }

        binding.codiBtn.setOnClickListener {
            val intent = Intent(this, CodiRecommendActivity::class.java)
            startActivity(intent)
        }

        binding.colorBtn.setOnClickListener {
            val intent = Intent(this, ColorRecommendActivity::class.java)
            startActivity(intent)
        }
    }

    private fun takePhoto() {
        val mImageCapture = imageCapture ?:return

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        mImageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Glide.with(this@CameraActivity)
                        .load(outputFileResults.savedUri)
                        .apply(
                            RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                        )
                        .into(binding.captureIV)

                    binding.fragmentContainer.visibility = View.GONE
                    binding.captureIV.visibility = View.VISIBLE
                    binding.aiLl.visibility = View.VISIBLE
                    binding.captureBtn.visibility = View.GONE
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(applicationContext, "실패", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
    override fun onBackPressed() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            finishAfterTransition()
        } else {
            super.onBackPressed()
        }
    }
}