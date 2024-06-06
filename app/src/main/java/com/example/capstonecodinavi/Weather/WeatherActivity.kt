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

    private var weatherList: ArrayList<JSONObject> = ArrayList()

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
                    Log.d("checkLatLon", "$lat $lon")
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
            address?.let {
                if (locality != null && adminArea != null && thoroughfare != null) {
                    binding.currentLocationTv.text = "${adminArea} ${locality} ${thoroughfare}"
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
                    addresses?.let {
                        if (locality != null && adminArea != null && thoroughfare != null) {
                            binding.currentLocationTv.text =
                                "${adminArea} ${locality} ${thoroughfare}"
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

    /*TODO : openweathermap에서 받아온 응답의 결과가 오늘의 날짜이면서 현재의 시간을 기점으로 이 시간 이후의 결과를 받아와야 함.
            1시간 단위로 가져오는 api로 바꾸기.
     */
    fun getCurrentWeather(lat: Double, lon: Double) {
        val url = "http://3.34.34.170:8080/weather?lat=${lat}&lon=${lon}"
        val request = object :
            StringRequest(
                Method.GET,
                url,
                Response.Listener { response ->
                    try {
                        Log.d("checkResponse", "$response")
                        var weatherStr: String
                        var weatherIconId: Int? = null
                        lateinit var temp: String
                        //lateinit var highTemp: String
                        //lateinit var lowTemp: String
                        lateinit var weather: String
                        lateinit var weather2: String

                        val jsonObject = JSONObject(response)
                        val jsonArray = jsonObject.getJSONArray("infoFromDateList")

                        for (i in 0 until jsonArray.length()) {
                            val item = jsonArray.getJSONObject(i)
                            val date = item.getString("date")

                            if (date.equals(substringNowDate)) {
                                weatherList.add(item.getJSONObject("info"))
                            }
                        }
                        Log.d("weatherList = ", "$weatherList")


                        for (i in 0 until weatherList.size) {
                            if (weatherList.get(i).getString("time")
                                    .equals(substringNowTime + "00")
                            ) {
                                weather = weatherList.get(i).getString("weather")
                                weather2 = weatherList.get(i).getString("precipitationType")
                                temp = weatherList.get(i).getString("temp")
                            }

                            nextNum = i + 1
                        }

                        if (weather.contains("흐림")) {
                            weatherIconId = R.drawable.cloudy
                        } else if (weather.contains("구름많음")) {
                            weatherIconId = R.drawable.cloudy
                        } else if (weather.contains("맑음")) {
                            weatherIconId = R.drawable.sunny
                        } else if (weather2.contains("비")) {
                            weatherIconId = R.drawable.rainy
                        } else if (weather2.contains("눈")) {
                            weatherIconId = R.drawable.snowy
                        } else if (weather2.contains("비 또는 눈")) {
                            weatherIconId = R.drawable.rainy
                        } else {
                            weatherIconId = R.drawable.rainy
                        }

                        if (weather.contains("흐림")) {
                            weatherStr = "흐림"
                        } else if (weather.contains("맑음")) {
                            weatherStr = "맑음"
                        } else if (weather.contains("구름많음")) {
                            weatherStr = "구름많음"
                        } else if (weather2.contains("없음")) {
                            weatherStr = ""
                        } else if (weather2.contains("비")) {
                            weatherStr = "비"
                        } else if (weather2.contains("눈")) {
                            weatherStr = "눈"
                        } else if (weather2.contains("비 또는 눈")) {
                            weatherStr = "비 또는 눈"
                        } else {
                            weatherStr = "소나기"
                        }

                        binding.weatherIV.setImageResource(weatherIconId!!)
                        binding.currentWeatherTv.text = "날씨 : $weatherStr"
                        binding.temperatureTv.text = "기온 : ${temp}º"
//                        binding.highLowTempTv.text = "최고 : ${}"
                        recommendCodi(temp.toDouble())
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { }
            ) {}
        request.setShouldCache(false) // 이전 결과가 있어도 새 요청하여 결과 보여주기
        requestQueue!!.add(request)
    }

    private fun recommendCodi(temp: Double) {
        val url = "http://3.34.34.170:8080/weather/clothInfo?temp=${temp}"
        val request = object : StringRequest(Method.GET, url, Response.Listener { response ->
            try {
                val jsonObject = JSONObject(response)
                val recInfo = jsonObject.getString("recInfo")
                binding.recommendClothTv.text = recInfo

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        },
            Response.ErrorListener { }) {}
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

}