package com.example.capstonecodinavi

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.capstonecodinavi.databinding.ActivityNewCameraBinding
import java.io.File
import java.util.concurrent.ExecutorService

class NewCamera : AppCompatActivity() {
    private lateinit var binding: ActivityNewCameraBinding
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private lateinit var photoFile: File
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewCameraBinding.inflate(layoutInflater)
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
            takePhoto()
        }

        binding.codiBtn.setOnClickListener {
            val intent = Intent(this, CodiActivity::class.java)
            startActivity(intent)
        }

        binding.colorBtn.setOnClickListener {
            val intent = Intent(this, ColorActivity::class.java)
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
                    Glide.with(this@NewCamera)
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