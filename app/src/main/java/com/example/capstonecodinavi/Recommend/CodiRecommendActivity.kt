package com.example.capstonecodinavi.Recommend

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.capstonecodinavi.Camera.AnalysisRequest
import com.example.capstonecodinavi.Camera.AnalysisResult
import com.example.capstonecodinavi.Camera.CameraActivity
import com.example.capstonecodinavi.Camera.RetrofitFlaskClient
import com.example.capstonecodinavi.Main.MainActivity
import com.example.capstonecodinavi.R
import com.example.capstonecodinavi.User.UserActivity
import com.example.capstonecodinavi.databinding.ActivityCodiBinding
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.RuntimeException
import java.util.UUID

class CodiRecommendActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCodiBinding

    companion object {
        var requestQueue: RequestQueue? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCodiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()

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
                        saveInfo(message2)
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

    private fun initUI() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(applicationContext)
        }
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

    fun saveInfo(result: String) {
        val url = "http://3.34.34.170:8080/cloth/recommendHistory"

        val body: JSONObject = JSONObject()
        try {
            body.put("parentId", intent.getStringExtra("parentId"))
            body.put("result", result)
        } catch (e: JSONException) {
            throw RuntimeException(e)
        }

        val request = object :
            JsonObjectRequest(
                Method.POST,
                url,
                body,
                com.android.volley.Response.Listener { response ->
                    try {
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                com.android.volley.Response.ErrorListener {  }
            ) {}
        request.setShouldCache(false)
        requestQueue!!.add(request)

    }
}