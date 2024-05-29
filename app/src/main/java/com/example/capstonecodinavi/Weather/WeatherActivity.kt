package com.example.capstonecodinavi.Weather

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
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.capstonecodinavi.Main.MainActivity
import com.example.capstonecodinavi.R
import com.example.capstonecodinavi.User.UserActivity
import com.google.android.gms.location.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.math.RoundingMode
import java.text.DecimalFormat
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

    lateinit var time: String

    val nowTime = LocalDateTime.now();
    val formatedNowTime = nowTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    val substringNowTime = formatedNowTime.substring(11 until 13)
    val substringNowTimeToInt = substringNowTime.toInt()

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
//        LocationRequest.create().apply {
//            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//        }

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

        binding.searchOtherLocationBtn.setOnClickListener {
            val intent = Intent(this, SearchOtherlocation::class.java)
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

    fun getCurrentLocation() {
        if (!checkPermissionForLocation(this)) return
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        fusedLocationProviderClient
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { success: Location? ->
                success?.let { location ->
                    lat = location.latitude
                    lon = location.longitude
                    Log.d("checkLatAndLog","$lat, $lon")
                    getCurrentAddress(lat!!, lon!!)
                    if (substringNowTimeToInt in 0..3) {
                        time = "03시"
                    } else if (substringNowTimeToInt in 4 .. 6) {
                        time = "06시"
                    } else if (substringNowTimeToInt in 7 .. 9) {
                        time = "09시"
                    } else if (substringNowTimeToInt in 10 .. 12) {
                        time = "12시"
                    } else if (substringNowTimeToInt in 13 .. 15) {
                        time = "15시"
                    } else if (substringNowTimeToInt in 16 .. 18) {
                        time = "18시"
                    } else if (substringNowTimeToInt in 19 .. 21) {
                        time = "21시"
                    } else if (substringNowTimeToInt in 22 .. 23) {
                        time = "21시"
                    }
                    Log.d("checkTime","$time")
                    getCurrentWeather(lat!!, lon!!, time)
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
                            binding.currentLocationTv.text = "${adminArea} ${locality} ${thoroughfare}"
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
    fun getCurrentWeather(lat: Double, lon: Double, time: String) {
        val url = "http://3.34.34.170:8080/weather?lat=${lat}&lon=${lon}&time=$time"
        Log.d("checkURL","$url")
        val request = object :
            StringRequest(
                Method.GET,
                url,
                Response.Listener { response ->
                    try {
                        var weatherStr: String

                        val jsonObject = JSONObject(response)
                        val weather = jsonObject.getString("weather")
                        val temp = jsonObject.getString("temp")

                        val weatherIconId = when {
                            weather.contains("Clouds") -> R.drawable.cloudy
                            weather.contains("Rain") -> R.drawable.rainy
                            weather.contains("Snow") -> R.drawable.snowy
                            else -> R.drawable.sunny
                        }

                        if (weather.contains("Clouds")) {
                            weatherStr = "흐림"
                        } else if (weather.contains("Rain")) {
                            weatherStr = "비"
                        } else if (weather.contains("Snow")) {
                            weatherStr = "눈"
                        } else {
                            weatherStr = "맑음"
                        }

                        binding.weatherIV.setImageResource(weatherIconId)
                        binding.currentWeatherTv1.text = "날씨 : ${weatherStr}"
                        binding.currentWeatherTv2.text = "기온 : ${temp}º"

                        recommendCodi(temp.toDouble())
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { }
            ) { }
        request.setShouldCache(false) // 이전 결과가 있어도 새 요청하여 결과 보여주기
        requestQueue!!.add(request)
    }

    private fun recommendCodi(temp: Double) {
        val url = "http://3.34.34.170:8080/weather/clothInfo?temp=${temp}"
        val request = object : StringRequest(Method.GET, url, Response.Listener { response ->
            try {
                val jsonObject = JSONObject(response)
                val recInfo = jsonObject.getString("recInfo")
                Log.d("checkcheck","$response")
                binding.recommendClothTv.text = recInfo

            } catch (e: JSONException) {
                e.printStackTrace()
            } },
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
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_PERMISSION_LOCATION
                )
                return false
            }
        } else {
            true
        }
    }
}