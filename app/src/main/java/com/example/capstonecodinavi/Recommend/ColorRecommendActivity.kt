package com.example.capstonecodinavi.Recommend

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.capstonecodinavi.Main.MainActivity
import com.example.capstonecodinavi.R
import com.example.capstonecodinavi.User.UserActivity
import com.example.capstonecodinavi.databinding.ActivityColorBinding
import org.json.JSONException
import org.json.JSONObject

class ColorRecommendActivity : AppCompatActivity() {
    private lateinit var binding: ActivityColorBinding
    private lateinit var color: String

    companion object {
        var requestQueue: RequestQueue? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityColorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(" ")
        initData()
        action()
        color = "파란색"
        //recommendColor(color)
    }
    private fun initData() {
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
//    private fun recommendColor(color: String) {
//        val url = "http://3.34.34.170:8080/color/recommend?color=${color}"
//        val request = object : StringRequest(Method.GET, url, Response.Listener { response ->
//            try {
//                val jsonObject = JSONObject(response)
//                val colorInfo = jsonObject.getString("matchColor")
//                binding.colorTv.text = colorInfo
//
//            } catch (e: JSONException) {
//                e.printStackTrace()
//            }
//
//        }, Response.ErrorListener { }) {}
//        request.setShouldCache(false) // 이전 결과가 있어도 새 요청하여 결과 보여주기
//        requestQueue!!.add(request)
//    }
}

