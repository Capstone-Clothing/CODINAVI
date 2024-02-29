package com.example.capstonecodinavi

import android.content.Intent
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.example.capstonecodinavi.databinding.ActivityOtherlocationBinding
import org.json.JSONException
import org.json.JSONObject
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OtherlocationActivity : AppCompatActivity() {
    lateinit var binding: ActivityOtherlocationBinding
    private var lat : Double? = null
    private var lng : Double? = null
    private var season: String? = null
    private var searchText: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtherlocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(" ")
        action()
        getLatLng()
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

    fun getLatLng(address: String) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            geocoder.getFromLocationName(address, 1)?.let {
                Location("").apply {
                    Log.d("check1000","$it")
                    lat = it[0].latitude
                    lng = it[0].longitude
                    getCurrentSeason()
                    getCurrentWeather(lat!!, lng!!)
                }
            }
        } catch (e : Exception){
            e.printStackTrace()
            getLatLng(address)
        }
    }

    fun getCurrentWeather(lat: Double, lon: Double) {
        var weatherStr: String
        val url = "https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${lon}&appid=2d360c1fe9d2bade8fc08a1679683e24"
        Log.d("check12345","$lat, $lon")
        val request = object :
            StringRequest(
                Method.GET,
                url,
                Response.Listener { response ->
                    try {
                        val jsonObject = JSONObject(response)
                        val weather = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main")
                        val kelvin = jsonObject.getJSONObject("main").getString("temp").toDouble()
                        var celsius = changeKelvinToCelsius(kelvin)

                        if (weather.contains("Rain")) {
                            weatherStr = "비"
                        } else if (weather.contains("Snow")) {
                            weatherStr = "눈"
                        } else if (weather.contains("Clouds")) {
                            weatherStr = "흐림"
                        } else {
                            weatherStr = "맑음"
                        }

                        binding.currentWeatherTv.text = "${searchText}의 계절은 ${season}, 날씨는 ${weatherStr}, 기온은 ${celsius}도 입니다."
                        recommendCodi(celsius.toDouble())
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { }
            ) { }
        request.setShouldCache(false) // 이전 결과가 있어도 새 요청하여 결과 보여주기
        WeatherActivity.requestQueue!!.add(request)
    }

    fun changeKelvinToCelsius(temp: Double): String {
        val changedTemp = (temp - 273.15)
        val df = DecimalFormat("#.#")
        df.roundingMode = RoundingMode.DOWN
        return df.format(changedTemp)
    }
    fun getCurrentSeason() {
        val now = System.currentTimeMillis()
        val date = Date(now)
        val simpleDateFormatDay = SimpleDateFormat("MM")
        val getMonth = simpleDateFormatDay.format(date)
        val getDate = """
             $getMonth
            """.trimIndent()
        if (getMonth == "12" || getMonth == "01" || getMonth == "02") season = "겨울"
        else if (getMonth == "03" || getMonth == "04" || getMonth == "05") season = "봄"
        else if (getMonth == "06" || getMonth == "07" || getMonth == "08") season = "여름"
        else season = "가을"
    }

    private fun recommendCodi(temp: Double) {
        if (temp >= 28) {
            binding.recommendClothTv.text = "상의는 민소매, 반팔을 추천해드리며 하의는 짧은 치마, 린넨 옷, 반바지를 추천드립니다."
        }
        else if (temp in 23.0..27.0) {
            binding.recommendClothTv.text = "상의는 얇은 셔츠, 반팔을 추천해드리며 하의는 반바지, 면바지를 추천드립니다."
        }
        else if (temp in 20.0..22.0) {
            binding.recommendClothTv.text = "상의는 블라우스, 긴팔티를 추천해드리며 하의는 면바지, 슬랙스를 추천드립니다."
        }
        else if (temp in 17.0..19.0) {
            binding.recommendClothTv.text = "상의는 얇은 가디건, 니트, 맨투맨, 후드를 추천해드리며 하의는 긴 바지를 추천드립니다."
        }
        else if (temp in 12.0..16.0) {
            binding.recommendClothTv.text = "상의는 자켓 또는 청자켓, 가디건, 니트를 추천해드리며 하의는 스타킹, 청바지를 추천드립니다."
        }
        else if (temp in 9.0..11.0) {
            binding.recommendClothTv.text = "상의는 트렌치 코트, 야상, 점퍼를 추천해드리며 하의는 스타킹, 기모바지를 추천드립니다."
        }
        else if (temp in 5.0..8.0) {
            binding.recommendClothTv.text = "상의는 울 코트 또는 가죽 옷과 히트텍을 추천해드리며 하의 또한 기모가 들어간 바지를 추천드립니다."
        }
        else {
            binding.recommendClothTv.text = "상의는 패딩, 두꺼운 코트, 누빔 옷, 기모가 들어간 소재, 그리고 목도리를 걸치시는 것을 추천드립니다."
        }
    }
}