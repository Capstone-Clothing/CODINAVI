package com.example.capstonecodinavi.Weather

import android.content.Intent
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.capstonecodinavi.Main.MainActivity
import com.example.capstonecodinavi.R
import com.example.capstonecodinavi.User.UserActivity
import com.example.capstonecodinavi.databinding.ActivityOtherlocationBinding
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority
import org.json.JSONException
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class OtherlocationActivity : AppCompatActivity() {
    lateinit var binding: ActivityOtherlocationBinding

    private var lat: Double? = null
    private var lon : Double? = null

    var nextNum: Int = 0
    val nowTime = LocalDateTime.now();
    val formatedNowTime = nowTime.format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss"))
    val substringNowDate = formatedNowTime.substring(0 until 8)
    val substringNowTime = formatedNowTime.substring(9 until 11)

    private var weatherInfoList: ArrayList<JSONObject> = ArrayList()

    companion object {
        var requestQueue: RequestQueue? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtherlocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(" ")
        initData()
        action()
        intent.getStringExtra("address")?.let {
            getLatLng(it)
        }
        binding.locationTv.text = intent.getStringExtra("address")
    }

    private fun action() {
        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.hourlyWeatherBtn.setOnClickListener {
            val intent = Intent(this, SearchOthertime::class.java)
            intent.putExtra("nextNum", nextNum)
            intent.putExtra("lat", lat)
            intent.putExtra("lon", lon)
            startActivity(intent)
        }

        binding.menuBottomNav.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
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

    private fun initData() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(applicationContext)
        }
    }

    fun getLatLng(address: String) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            geocoder.getFromLocationName(address, 1)?.let {
                Location("").apply {
                    lat = it[0].latitude
                    lon = it[0].longitude
                    getCurrentWeather(lat!!, lon!!)
                }
            }
        } catch (e : Exception){
            e.printStackTrace()
            getLatLng(address)
        }
    }

    fun getCurrentWeather(lat: Double, lon: Double) {
        val url = "http://3.34.34.170:8080/weather?lat=$lat&lon=$lon"
        val request = object :
            StringRequest(
                Method.GET,
                url,
                Response.Listener { response ->
                    try {
                        var weatherStr: String
                        var weatherIconId: Int? = null
                        lateinit var temp: String
                        lateinit var lowTemp: String
                        lateinit var highTemp: String
                        lateinit var weather: String
                        lateinit var weather2: String

                        val jsonObject = JSONObject(response)
                        val jsonArray = jsonObject.getJSONArray("infoFromDateList")

                        for (i in 0 until jsonArray.length()) {
                            val item = jsonArray.getJSONObject(i)
                            val date = item.getString("date")

                            if (date.equals(substringNowDate)) {
                                weatherInfoList.add(item.getJSONObject("info"))
                            }
                        }

                        Log.d("weatherList = ", "$weatherInfoList")

                        for (i in 0 until weatherInfoList.size) {
                            if (weatherInfoList.get(i).getString("time")
                                    .equals(substringNowTime + "00")
                            ) {
                                weather = weatherInfoList.get(i).getString("weather")
                                weather2 = weatherInfoList.get(i).getString("precipitationType")
                                temp = weatherInfoList.get(i).getString("temp")
                                nextNum = i + 1
                            }
                        }

                        weatherIconId = getWeatherIconId(weather, weather2)
                        weatherStr = getWeatherStr(weather, weather2)

                        lowTemp = jsonArray.getJSONObject(0).getString("lowTemp")
                        highTemp = jsonArray.getJSONObject(0).getString("highTemp")

                        binding.weatherIV.setImageResource(weatherIconId!!)
                        binding.weatherTv.text = "날씨 : $weatherStr"
                        binding.temperatureTv.text = "기온 : ${temp}º"
                        binding.highLowTempTv.text = "최고 : ${highTemp}º / 최저 : ${lowTemp}º"
                        recommendCodi(temp.toDouble(), "여자")
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { }
            ) { }
        request.setShouldCache(false) // 이전 결과가 있어도 새 요청하여 결과 보여주기
        WeatherActivity.requestQueue!!.add(request)
    }

    private fun recommendCodi(temp: Double, gender: String) {
        val url = "http://3.34.34.170:8080/weather/clothInfo?temp=${temp}&gender=${gender}"
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

    fun getWeatherIconId(weather: String, weather2: String): Int {

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
                weatherIconId = R.drawable.overcast
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

    fun getWeatherStr(weather: String, weather2: String): String {

        lateinit var weatherStr: String

        if (weather.contains("흐림")) {
            if (weather2.contains("비")) {
                weatherStr = "비"
            } else if (weather2.contains("눈")) {
                weatherStr = "눈"
            } else if (weather2.contains("비 또는 눈")) {
                weatherStr = "비 또는 눈"
            } else if (weather2.contains("소나기")) {
                weatherStr = "소나기"
            } else {
                weatherStr = "흐림"
            }
        } else if (weather.contains("구름많음")) {
            if (weather2.contains("비")) {
                weatherStr = "비"
            } else if (weather2.contains("눈")) {
                weatherStr = "눈"
            } else if (weather2.contains("비 또는 눈")) {
                weatherStr = "비 또는 눈"
            } else if (weather2.contains("소나기")) {
                weatherStr = "소나기"
            } else {
                weatherStr = "구름많음"
            }
        } else if (weather.contains("맑음")) {
            if (weather2.contains("비")) {
                weatherStr = "비"
            } else if (weather2.contains("눈")) {
                weatherStr = "눈"
            } else if (weather2.contains("비 또는 눈")) {
                weatherStr = "비 또는 눈"
            } else if (weather2.contains("소나기")) {
                weatherStr = "소나기"
            } else {
                weatherStr = "맑음"
            }
        }
        return weatherStr
    }
}