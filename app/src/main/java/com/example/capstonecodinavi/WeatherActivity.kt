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
import android.os.Build
import android.provider.Telephony.Mms.Addr
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
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

    //위치
    private val REQUEST_PERMISSION_LOCATION = 10
    private var lat: Double? = null
    private var lon: Double? = null
    private var address: Address? = null
    private var season: String? = null
    private var addrArea: String? = null
    private var addrLocal: String? = null
    private var addrFare: String? = null
    //날씨 및 기온
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
            finish()
        }

        binding.profileBtn.setOnClickListener {
            val intent = Intent(this, UserActivity::class.java)
            startActivity(intent)
        }
    }
    private fun getCurrentLocation() {
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
                    addrArea = getAddress(lat!!, lon!!)?.adminArea
                    addrLocal = getAddress(lat!!, lon!!)?.locality
                    addrFare = getAddress(lat!!, lon!!)?.thoroughfare

                    Log.d("checkcheck2","${addrArea} ${addrLocal} ${addrFare}")
                    Log.d("hihi", "$address")
//                    Log.d("checkcheck","${address!!.get(1)} ${address!!.get(2)} ${address!!.get(3)}")
                    getCurrentWeather()
                    getCurrentSeason()
                }
            }
    }
    private fun getAddress(lat: Double, lng: Double): Address? {
        lateinit var address: Address

        return try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val geocodeListener = @RequiresApi(33) object: Geocoder.GeocodeListener {
                override fun onGeocode(addresses: MutableList<Address>) {
                    Log.d("addresscheck","$addresses")
                    address = addresses[0]
                    address
                }
            }
            geocoder.getFromLocation(lat, lng, 1,geocodeListener)
            address
        } catch (e: IOException) {
            Toast.makeText(this, "주소를 가져올 수 없습니다", Toast.LENGTH_SHORT).show()
            null
        }
    }
    private fun getCurrentWeather() {
        val url = "https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${lon}&appid=2d360c1fe9d2bade8fc08a1679683e24"
        val request = object :
            StringRequest(
                Method.GET,
                url,
                Response.Listener { response ->
                    try {
                        // JSON 데이터 가져오기
                        val jsonObject = JSONObject(response)
                        val weather = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main")
                        val kelvin = jsonObject.getJSONObject("main").getString("temp").toDouble()
                        val celsius = changeKelvinToCelsius(kelvin)
                        var weatherStr: String

                        //날씨
                        if (weather.contains("Rain")) {
                            weatherStr = "비"
                        } else if (weather.contains("Snow")) {
                            weatherStr = "눈"
                        } else if (weather.contains("Clouds")) {
                            weatherStr = "흐림"
                        } else {
                            weatherStr = "맑음"
                        }

                        binding.instructionTv.text = "현재 위치는 ${addrArea} ${addrLocal} ${addrFare} 입니다. \n계절은 ${season}이고 날씨는 ${weatherStr}이며 기온은 ${celsius}도 입니다."
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { }
            ) { }

        request.setShouldCache(false) // 이전 결과가 있어도 새 요청하여 결과 보여주기
        requestQueue!!.add(request)
    }
    private fun getCurrentSeason() {
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
    private fun changeKelvinToCelsius(temp: Double): String {
        val changedTemp = (temp - 273.15)
        val df = DecimalFormat("#.#")
        df.roundingMode = RoundingMode.DOWN
        return df.format(changedTemp)
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
    private fun checkPermissionForLocation(context: Context): Boolean {
        if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true
        } else {
            // 권한이 없으므로 권한 요청 알림 보내기
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_PERMISSION_LOCATION)
            return false
        }
    }
}