package com.example.capstonecodinavi.Recommend

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.capstonecodinavi.Camera.AnalysisRequest
import com.example.capstonecodinavi.Camera.AnalysisResult
import com.example.capstonecodinavi.Camera.CameraActivity
import com.example.capstonecodinavi.Camera.RetrofitFlaskClient
import com.example.capstonecodinavi.Main.MainActivity
import com.example.capstonecodinavi.R
import com.example.capstonecodinavi.User.UserActivity
import com.example.capstonecodinavi.databinding.ActivityCodiBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class CodiRecommendActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCodiBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCodiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageId = intent.getStringExtra("imageId")!!
        val request = AnalysisRequest(
            bucket_name = "codinavi-image",
            image_key = imageId
        )

        val call = RetrofitFlaskClient.instance.getAnalysisResult(request)
        call.enqueue(object : Callback<AnalysisResult> {
            override fun onResponse(call: Call<AnalysisResult>, response: Response<AnalysisResult>) {
                Log.d("CameraFragment", "Response received: ${response.body()}")
                if (response.isSuccessful) {
                    val result = response.body()
                    result?.let {
                        val clothingItem = it.result[0]
                        val message2 = "${clothingItem.어울리는상의or하의추천}을 추천드립니다."
                        binding.codiTv.text = message2
                    }
                } else {
                    Log.e("CameraFragment", "Failed to get analysis result: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<AnalysisResult>, t: Throwable) {
                Log.e("CameraFragment", "Error: ${t.message}")
            }
        })
        setTitle(" ")
        action()
    }

    private fun action() {
        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.confirmBtn.setOnClickListener {
            val intent = Intent(this, ConfirmActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
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
}