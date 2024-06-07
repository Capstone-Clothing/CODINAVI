package com.example.capstonecodinavi.Weather

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.capstonecodinavi.Main.MainActivity
import com.example.capstonecodinavi.R
import com.example.capstonecodinavi.User.UserActivity
import com.example.capstonecodinavi.databinding.ActivitySearchOthertimeBinding
import org.json.JSONException
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SearchOthertime : AppCompatActivity() {
    lateinit var binding: ActivitySearchOthertimeBinding

    val nowTime = LocalDateTime.now();
    val formatedNowTime = nowTime.format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss"))
//    val substringNowDate = formatedNowTime.substring(0 until 8)
//    val substringNowTime = formatedNowTime.substring(9 until 11)
    var nextNum: Int = 0

    private var weatherInfoList: ArrayList<JSONObject> = ArrayList()

    companion object {
        var requestQueue: RequestQueue? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchOthertimeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        nextNum = intent.getIntExtra("nextNum",0)
        val lat = intent.getDoubleExtra("lat", 0.0)
        val lon = intent.getDoubleExtra("lon", 0.0)

        initData()
        action()
        setTitle(" ")
        getHourlyWeather(lat, lon)
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

        binding.hourlyBtn.setOnClickListener {
            val intent = Intent(this, OthertimeActivity::class.java)
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

    fun getHourlyWeather(lat: Double, lon: Double) {
        val url = "http://3.34.34.170:8080/weather?lat=${lat}&lon=${lon}"
        val request = object :
            StringRequest(
                Method.GET,
                url,
                Response.Listener { response ->
                    try {
                        val weatherList: ArrayList<String> = ArrayList()
                        val weatherList2: ArrayList<String> = ArrayList()
                        val tempList: ArrayList<String> = ArrayList()

                        val jsonObject = JSONObject(response)
                        val jsonArray = jsonObject.getJSONArray("infoFromDateList")

                        for (i in nextNum until nextNum + 14) {
                            val item = jsonArray.getJSONObject(i)
                            weatherInfoList.add(item.getJSONObject("info"))
                        }

                        for (i in 0 until weatherInfoList.size) {
                            weatherList.add(weatherInfoList.get(i).getString("weather"))
                            weatherList2.add(weatherInfoList.get(i).getString("precipitationType"))
                            tempList.add(weatherInfoList.get(i).getString("temp"))
                        }

                        binding.timeTv.text = weatherInfoList.get(0).getString("time").substring(0 until 2) + "시"
                        binding.weatherIV.setImageResource(getWeatherIconId(weatherList.get(0), weatherList2.get(0)))
                        binding.tempTv.text = weatherInfoList.get(0).getString("temp") + "º"
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { }
            ) { }
        request.setShouldCache(false) // 이전 결과가 있어도 새 요청하여 결과 보여주기
        WeatherActivity.requestQueue!!.add(request)
    }

    fun getWeatherIconId(weather:String, weather2:String): Int {

        var weatherIconId: Int = 0

        if (weather.contains("흐림")) {
            if (weather2.contains("비")) {
                weatherIconId = R.drawable.rainy
            } else if (weather2.contains("눈")) {
                weatherIconId = R.drawable.snowy
            } else if (weather2.contains("비 또는 눈")) {
                weatherIconId = R.drawable.rainy
            } else if (weather2.contains("소나기")) {
                weatherIconId = R.drawable.rainy
            } else {
                weatherIconId = R.drawable.cloudy
            }
        } else if (weather.contains("구름많음")) {
            if (weather2.contains("비")) {
                weatherIconId = R.drawable.rainy
            } else if (weather2.contains("눈")) {
                weatherIconId = R.drawable.snowy
            } else if (weather2.contains("비 또는 눈")) {
                weatherIconId = R.drawable.rainy
            } else if (weather2.contains("소나기")) {
                weatherIconId = R.drawable.rainy
            } else {
                weatherIconId = R.drawable.cloudy
            }
        } else if (weather.contains("맑음")) {
            if (weather2.contains("비")) {
                weatherIconId = R.drawable.rainy
            } else if (weather2.contains("눈")) {
                weatherIconId = R.drawable.snowy
            } else if (weather2.contains("비 또는 눈")) {
                weatherIconId = R.drawable.rainy
            } else if (weather2.contains("소나기")) {
                weatherIconId = R.drawable.rainy
            } else {
                weatherIconId = R.drawable.sunny
            }
        }

        return weatherIconId
    }

}