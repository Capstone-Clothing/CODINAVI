package com.example.capstonecodinavi.Recommend

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.capstonecodinavi.Camera.CameraFragment
import com.example.capstonecodinavi.Main.MainActivity
import com.example.capstonecodinavi.User.UserActivity
import com.example.capstonecodinavi.databinding.ActivityConfirmBinding
import java.io.File
import java.util.concurrent.ExecutorService
import com.example.capstonecodinavi.Camera.ObjectDetectorHelper
import com.example.capstonecodinavi.R

class ConfirmActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConfirmBinding
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private lateinit var photoFile: File
    private lateinit var objectDetectorHelper: ObjectDetectorHelper

    companion object {
        var requestQueue: RequestQueue? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(" ")

        initUI()
        action()

        if (savedInstanceState == null) {
            val navHostFragment = NavHostFragment.create(R.navigation.nav_graph)
            supportFragmentManager.beginTransaction()
                .replace(binding.fragmentContainer.id, navHostFragment)
                .setPrimaryNavigationFragment(navHostFragment)  // 여기에 추가
                .commit()
        }

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun initUI() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(applicationContext)
        }
    }

    private fun action() {
        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.captureBtn.setOnClickListener {
            val navFragment = supportFragmentManager.findFragmentById(binding.fragmentContainer.id) as NavHostFragment
            val cameraFragment = navFragment.childFragmentManager.primaryNavigationFragment as? CameraFragment
            if (cameraFragment != null) {
                imageCapture = cameraFragment.getImageCapture()
                photoFile = File(
                    applicationContext.cacheDir,
                    "newImage.jpg"
                )
                takePhoto()
                Log.d("check test", "$imageCapture")
            } else {
                Log.e("CameraActivity", "CameraFragment not found")
            }
        }

        binding.menuBottomNav.setOnItemSelectedListener { menuItem->
            when(menuItem.itemId) {
                R.id.menu_home -> {
                    // 홈 버튼 클릭 시 MainActivity로 이동
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menu_user -> {
                    val intent = Intent(this, UserActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    private fun takePhoto() {
        val mImageCapture = imageCapture ?: return

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        mImageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Glide.with(this@ConfirmActivity)
                        .load(photoFile)
                        .apply(
                            RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                        )
//                        .into(binding.captureIV)

                    binding.fragmentContainer.visibility = View.GONE
                    binding.recogtext.visibility = View.GONE
//                    binding.captureIV.visibility = View.VISIBLE
                    binding.textView2.visibility = View.VISIBLE
                    binding.captureBtn.visibility = View.GONE

                    val navFragment = supportFragmentManager.findFragmentById(binding.fragmentContainer.id) as NavHostFragment
                    val cameraFragment = navFragment.childFragmentManager.primaryNavigationFragment as? CameraFragment
                    cameraFragment?.uploadImage(photoFile)
                }
                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(applicationContext, "사진 전송 실패", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    fun updateTextView(message: String) {
        binding.recogtext.text = message
    }

    fun updateAnalysisResult(message2: String){
        binding.textView2.text = message2

    }


}