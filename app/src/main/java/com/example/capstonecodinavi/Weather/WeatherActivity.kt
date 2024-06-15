package com.example.capstonecodinavi.Weather

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.capstonecodinavi.Main.MainActivity
import com.example.capstonecodinavi.R
import com.example.capstonecodinavi.User.UserActivity
import com.example.capstonecodinavi.databinding.ActivityWeatherBinding
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class WeatherActivity : AppCompatActivity() {
    lateinit var binding: ActivityWeatherBinding

    private val REQUEST_PERMISSION_LOCATION = 10
    private var lat: Double? = null
    private var lon: Double? = null
    private var adminArea: String? = null
    private var locality: String? = null
    private var thoroughfare: String? = null
    private var timeInterval: Long = 3

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
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        action()
        getCurrentLocation()
        setTitle(" ")
    }

    private fun initData() {
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, timeInterval).apply {

        }.build()
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(applicationContext)
        }
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

        binding.searchOtherLocationBtn.setOnClickListener {
            val intent = Intent(this, SearchOtherlocation::class.java)
            startActivity(intent)
        }

        binding.menuBottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
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

    fun getCurrentLocation() {
        if (!checkPermissionForLocation(this)) return
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationProviderClient
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { success: Location? ->
                success?.let { location ->
                    lat = location.latitude
                    lon = location.longitude
                    Log.d("checkLatAndLog", "$lat, $lon")
                    getCurrentAddress(lat!!, lon!!)
                    getCurrentWeather(lat!!, lon!!)
                }
            }
    }

    fun getCurrentAddress(lat: Double, lng: Double) {
        if (Build.VERSION.SDK_INT < 33) {
            var address: List<Address>
            val geocoder = Geocoder(this, Locale.getDefault())
            address = geocoder.getFromLocation(lat, lng, 7) as List<Address>
            for (addr in address) {
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
            address.let {
                if (locality != null && adminArea != null && thoroughfare != null) {
                    if (adminArea == locality) {
                        binding.currentLocationTv.text ="${adminArea} ${thoroughfare}"
                    } else if (locality == thoroughfare) {
                        binding.currentLocationTv.text ="${adminArea} ${thoroughfare}"
                    } else {
                        binding.currentLocationTv.text = "${adminArea} ${locality} ${thoroughfare}"
                    }
                } else if (locality == null) {
                    binding.currentLocationTv.text = "${adminArea} ${thoroughfare}"
                } else if (adminArea == null) {
                    binding.currentLocationTv.text = "${locality} ${thoroughfare}"
                } else {
                    binding.currentLocationTv.text = "${adminArea} ${locality}"
                }
            }
        } else {
            val geocoder = Geocoder(this, Locale.getDefault())
            try {
                val geocodeListener = Geocoder.GeocodeListener { addresses ->
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
                    addresses.let {
                        if (locality != null && adminArea != null && thoroughfare != null) {
                            if (adminArea == locality) {
                                binding.currentLocationTv.text ="${adminArea} ${thoroughfare}"
                            } else if (locality == thoroughfare) {
                                binding.currentLocationTv.text ="${adminArea} ${thoroughfare}"
                            } else {
                                binding.currentLocationTv.text = "${adminArea} ${locality} ${thoroughfare}"
                            }
                        } else if (locality == null) {
                            binding.currentLocationTv.text = "${adminArea} ${thoroughfare}"
                        } else if (adminArea == null) {
                            binding.currentLocationTv.text = "${locality} ${thoroughfare}"
                        } else {
                            binding.currentLocationTv.text = "${adminArea} ${locality}"
                        }
                    }
                }
                geocoder.getFromLocation(lat, lng, 7, geocodeListener)
            } catch (e: IOException) {
                Toast.makeText(this, "주소를 가져올 수 없습니다", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun getCurrentWeather(lat: Double, lon: Double) {
        val url = "http://3.34.34.170:8080/weather?lat=${lat}&lon=${lon}"
        Log.d("checkURL", "$url")
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
                        binding.currentWeatherTv.text = "날씨 : $weatherStr"
                        binding.temperatureTv.text = "기온 : ${temp}º"
                        binding.highLowTempTv.text = "최고 : ${highTemp}º / 최저 : ${lowTemp}º"
                        recommendCodi(temp.toDouble(), "여자")
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { }
            ) {}
        request.setShouldCache(false) // 이전 결과가 있어도 새 요청하여 결과 보여주기
        requestQueue!!.add(request)
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
                Toast.makeText(this, "권한이 없어 해당 기능을 실행할 수 없습니다", Toast.LENGTH_SHORT).show()
            }
        }
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

    fun checkPermissionForLocation(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                return true
            } else {
                // 권한이 없으므로 권한 요청 알림 보내기
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSION_LOCATION
                )
                return false
            }
        } else {
            true
        }
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