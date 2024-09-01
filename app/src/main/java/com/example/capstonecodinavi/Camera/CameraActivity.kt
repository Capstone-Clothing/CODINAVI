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
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.capstonecodinavi.Recommend.CodiRecommendActivity
import com.example.capstonecodinavi.Recommend.ColorRecommendActivity
import com.example.capstonecodinavi.Main.MainActivity
import com.example.capstonecodinavi.R
import com.example.capstonecodinavi.Recommend.SearchOccasionActivity
import com.example.capstonecodinavi.User.UserActivity
import com.example.capstonecodinavi.databinding.ActivityCameraBinding
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.lang.RuntimeException
import java.util.concurrent.ExecutorService

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private lateinit var photoFile: File
    private lateinit var objectDetectorHelper: ObjectDetectorHelper

    lateinit var imageId2: String

    companion object {
        var requestQueue: RequestQueue? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(" ")

        initUI()

        if (savedInstanceState == null) {
            val navHostFragment = NavHostFragment.create(R.navigation.nav_graph)
            supportFragmentManager.beginTransaction()
                .replace(binding.fragmentContainer.id, navHostFragment)
                .setPrimaryNavigationFragment(navHostFragment)  // 여기에 추가
                .commit()
        }

        action()
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

        binding.codiBtn.setOnClickListener {
            val intent = Intent(this, CodiRecommendActivity::class.java)
            intent.putExtra("imageId", imageId2)
            startActivity(intent)
        }

        binding.colorBtn.setOnClickListener {
            val intent = Intent(this, ColorRecommendActivity::class.java)
            intent.putExtra("imageId", imageId2)
            startActivity(intent)
        }

        binding.occasionCodiBtn.setOnClickListener {
            val intent = Intent(this, SearchOccasionActivity::class.java)
            startActivity(intent)
        }

        binding.menuBottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_home -> {
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
                    Glide.with(this@CameraActivity)
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
                    binding.codiBtn.visibility = View.VISIBLE
                    binding.colorBtn.visibility = View.VISIBLE
                    binding.captureBtn.visibility = View.GONE
                    binding.occasionCodiBtn.visibility = View.VISIBLE

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
        Log.d("CameraActivity", "Updating UI with message: $message2")
        binding.textView2.text = message2

        val typeRegex = Regex("종류는 (.+)이고,")
        val patternRegex = Regex("무늬는 (.+)이며,")
        val colorRegex = Regex("색상은 (.+)입니다")

        val typeMatch = typeRegex.find(message2)?.groups?.get(1)?.value ?: "알 수 없음"
        val patterMatch = patternRegex.find(message2)?.groups?.get(1)?.value ?: "알 수 없음"
        val colorMatch = colorRegex.find(message2)?.groups ?.get(1)?.value ?: "알 수 없음"


        saveInfo(typeMatch, patterMatch, colorMatch)
    }
    fun getImageId(imageId: String) {
        imageId2 = imageId
    }

    fun saveInfo(type: String, pattern: String, color: String) {
        val url = "http://3.34.34.170:8080/cloth/history"

        val body: JSONObject = JSONObject()
        try {
            body.put("color", color)
            body.put("pattern", pattern)
            body.put("type", type)
        } catch (e: JSONException) {
            throw RuntimeException(e)
        }

        val request = object :
        JsonObjectRequest(
            Method.POST,
            url,
            body,
            Response.Listener { response ->
                try {
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {  }
        ) {}
        request.setShouldCache(false)
        requestQueue!!.add(request)
    }
}
