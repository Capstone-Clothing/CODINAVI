package com.example.capstonecodinavi.Weather

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.capstonecodinavi.Main.MainActivity
import com.example.capstonecodinavi.R
import com.example.capstonecodinavi.User.UserActivity
import com.example.capstonecodinavi.databinding.ActivityOthertimeBinding
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority
import org.json.JSONException
import org.json.JSONObject

class OthertimeActivity : AppCompatActivity() {
    lateinit var binding: ActivityOthertimeBinding
    lateinit var weather: Weather

    companion object {
        var requestQueue: RequestQueue? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOthertimeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        weather = intent.getSerializableExtra("weather") as Weather

        initData()
        action()
        connectData(weather)
        recommendCodi(weather.temp.toDouble(), "여자")
        setTitle(" ")
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

    fun connectData(weather: Weather) {
        binding.timeTv.text = "${weather.time.substring(0 until 2)}시"
        binding.humidityTv.text = "습도 : ${weather.humidity}%"
        binding.weatherIV.setImageResource(weather.weatherIconId)
        binding.precipitationTv.text = "강수량 : ${weather.precipitation}"
        binding.precipitationProbTv.text = "강수확률 : ${weather.precipitationProbability}%"
        binding.temperatureTv.text = "기온 : ${weather.temp}º"
        binding.currentWeatherTv.text = "날씨 : ${weather.weatherText}"
    }

    private fun recommendCodi(temp: Double, gender: String) {
        val url = "http://3.34.34.170:8080/weather/clothInfo?temp=${temp}&gender=${gender}"
        Log.d("checkURL2","$url")
        val request = object :
            StringRequest(
                Method.GET,
                url,
                Response.Listener { response ->
                    try {
                        val jsonObject = JSONObject(response)
                        val codi = jsonObject.getString("codi")
                        val clothRec = jsonObject.getString("clothRec")
                        binding.recommendClothTv.text = codi
                        binding.recommendItemTv.text = "추천 아이템 : $clothRec"

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { }) {}
        request.setShouldCache(false) // 이전 결과가 있어도 새 요청하여 결과 보여주기
        requestQueue!!.add(request)
    }
}
