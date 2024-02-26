package com.example.capstonecodinavi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.capstonecodinavi.databinding.ActivityWeatherBinding
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherActivity : AppCompatActivity() {
    lateinit var binding: ActivityWeatherBinding

    private val REQUEST_PERMISSION_LOCATION = 10
    private var lat: Double? = null
    private var lon: Double? = null
    private var season: String? = null
    private var adminArea: String? = null
    private var locality: String? = null
    private var thoroughfare: String? = null
    companion object {
        var requestQueue: RequestQueue? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        action()
        getCurrentLocation()
        setTitle(" ")
    }
    private fun initData() {
        LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(applicationContext)
        }
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

        binding.searchOtherLocationBtn.setOnClickListener {
            val intent = Intent(this, SearchOtherlocation::class.java)
            startActivity(intent)
        }
    }
    fun getCurrentLocation() {
        if (!checkPermissionForLocation(this)) return
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        fusedLocationProviderClient
            .getCurrentLocation(PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { success: Location? ->
                success?.let { location ->
                    lat = location.latitude
                    lon = location.longitude
//                    lat = 35.2070205
//                    lon = 129.1025573
                    getCurrentAddress(lat!!, lon!!)
                    getCurrentSeason()
                    getCurrentWeather(lat!!, lon!!)
                }
            }
    }
    fun getCurrentAddress(lat: Double, lng: Double) {
        var address: Address
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val geocodeListener = Geocoder.GeocodeListener { addresses ->
                address = addresses[5]
                Log.d("check123", "$addresses")
                for (addr in addresses) {
                    if (addr.adminArea != null && adminArea == null) {
                        adminArea = addr.adminArea
                    }
                    if (addr.locality != null && locality == null) {
                        locality = addr.locality
                    }
                    if (addr.thoroughfare != null && thoroughfare == null) {
                        thoroughfare = addr.thoroughfare
                    }
                }
                address?.let {
                    if (locality != null && adminArea != null && thoroughfare != null) {
                        binding.currentLocationTv.text = "현재 위치는 ${adminArea} ${locality} ${thoroughfare} 입니다."
                    } else if (locality == null) {
                        binding.currentLocationTv.text = "현재 위치는 ${adminArea} ${thoroughfare} 입니다."
                    } else if (adminArea == null) {
                        binding.currentLocationTv.text = "현재 위치는 ${locality} ${thoroughfare} 입니다."
                    } else {
                        binding.currentLocationTv.text = "현재 위치는 ${adminArea} ${locality} 입니다."
                    }
                }
            }
            geocoder.getFromLocation(lat, lng, 7, geocodeListener)
        } catch (e: IOException) {
            Toast.makeText(this, "주소를 가져올 수 없습니다", Toast.LENGTH_SHORT).show()
        }

    }
    fun getCurrentWeather(lat: Double, lon: Double) {
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
                        var weatherStr: String

                        if (weather.contains("Rain")) {
                            weatherStr = "비"
                        } else if (weather.contains("Snow")) {
                            weatherStr = "눈"
                        } else if (weather.contains("Clouds")) {
                            weatherStr = "흐림"
                        } else {
                            weatherStr = "맑음"
                        }

                        binding.currentWeatherTv.text = "계절은 ${season}, 날씨는 ${weatherStr}, 기온은 ${celsius}도 입니다."
                        recommendCodi(celsius.toDouble())
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { }
            ) { }
        request.setShouldCache(false) // 이전 결과가 있어도 새 요청하여 결과 보여주기
        requestQueue!!.add(request)
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
    fun changeKelvinToCelsius(temp: Double): String {
        val changedTemp = (temp - 273.15)
        val df = DecimalFormat("#.#")
        df.roundingMode = RoundingMode.DOWN
        return df.format(changedTemp)
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
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                Log.d("ttt", "onRequestPermissionResult() _ 권한 허용 거부")
                Toast.makeText(this, "권한이 없어 해당 기능을 실행할 수 없습니다", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun checkPermissionForLocation(context: Context): Boolean {
        if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true
        } else {
            // 권한이 없으므로 권한 요청 알림 보내기
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_PERMISSION_LOCATION)
            return false
        }
    }
}