package com.example.capstonecodinavi.Recommend

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.capstonecodinavi.Main.MainActivity
import com.example.capstonecodinavi.User.UserActivity
import com.example.capstonecodinavi.databinding.ActivityConfirmBinding
import java.io.File
import java.util.concurrent.ExecutorService
import com.example.capstonecodinavi.Camera.ObjectDetectorHelper

class ConfirmActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConfirmBinding
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private lateinit var photoFile: File
    private lateinit var objectDetectorHelper: ObjectDetectorHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(" ")
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

    }

    private fun takePhoto() {
        val mImageCapture = imageCapture ?:return

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        mImageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Glide.with(this@ConfirmActivity)
                        .load(outputFileResults.savedUri)
                        .apply(
                            RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                        )
                        .into(binding.captureIV)

                    binding.fragmentContainer.visibility = View.GONE
                    binding.recogtext.visibility = View.GONE
                    binding.captureIV.visibility = View.VISIBLE
                    binding.textView2.visibility = View.VISIBLE
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(applicationContext, "실패", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }


    override fun onBackPressed() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
            finish()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else {
            super.onBackPressed()
        }
    }
}