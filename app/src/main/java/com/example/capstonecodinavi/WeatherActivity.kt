package com.example.capstonecodinavi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.capstonecodinavi.databinding.ActivityWeatherBinding
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import org.json.JSONException
import org.json.JSONObject
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date

class WeatherActivity : AppCompatActivity() {
    lateinit var binding: ActivityWeatherBinding

    //위치
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null // 현재 위치를 가져오기 위한 객체
    internal lateinit var mLocationRequest: LocationRequest //위치 요청에 대한 설정을 정의하는 객체. 현재 위치를 요청할 때 위치 정확도, 업데이트 간격 등을 설정함.
    private val REQUEST_PERMISSION_LOCATION = 10
    private var lat: Double? = null
    private var lon: Double? = null

    //날씨 및 기온
    companion object {
        var requestQueue: RequestQueue? = null
    }
    private var setWeather: String? = null
    private var setTemp: String? = null
    private var setSeason: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setting()
        if(checkPermissionForLocation(this)) {
            getCurrentLocation()
        }
        action()
    }
    private fun setting() {
        mLocationRequest = LocationRequest.create().apply {
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

        binding.Button.setOnClickListener {
            val now = System.currentTimeMillis()
            val date = Date(now)
            val simpleDateFormatDay = SimpleDateFormat("MM")
            val getMonth = simpleDateFormatDay.format(date)
            val getDate = """
                $getMonth
            """.trimIndent()

            if (getMonth == "12" || getMonth == "01" || getMonth == "02") {
                setSeason = "겨울"
            }
            else if (getMonth == "03" || getMonth == "04" || getMonth == "05") {
                setSeason = "봄"
            }
            else if (getMonth == "06" || getMonth == "07" || getMonth == "08") {
                setSeason = "여름"
            }
            else {
                setSeason = "가을"
            }
            getCurrentWeather()
        }
    }
    private fun getCurrentLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        mFusedLocationProviderClient!!.getCurrentLocation(PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener {success: Location? ->
                success?.let { location ->  //success가 null이 아닌 경우
                    lat = location.latitude
                    lon = location.longitude
                    Toast.makeText(this,"현재 위도 : ${lat}",Toast.LENGTH_SHORT).show()
                    Toast.makeText(this,"현재 경도 : ${lon}",Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun getCurrentWeather() {
        val url = "https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${lon}&appid=2d360c1fe9d2bade8fc08a1679683e24"
        val request = object : StringRequest(Request.Method.GET, url,
            Response.Listener { response ->
                try {
                    // JSON 데이터 가져오기
                    val jsonObject = JSONObject(response)
                    val weather = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main")
                    val temp = jsonObject.getJSONObject("main").getString("temp").toDouble()
                    val changedTemp = changeTemp(temp)

                    //날씨
                    if (weather.contains("Rain")) {
                        setWeather = "비"
                    } else if (weather.contains("Snow")) {
                        setWeather = "눈"
                    } else if (weather.contains("Clouds")) {
                        setWeather = "흐림"
                    } else {
                        setWeather = "맑음"
                    }

                    //기온
                    setTemp = changedTemp

                    binding.instructionTv.text = "현재 계절은 ${setSeason}이고 날씨는 ${setWeather}이며 기온은 ${setTemp}입니다."
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { }) { }
        request.setShouldCache(false) // 이전 결과가 있어도 새 요청하여 결과 보여주기
        requestQueue!!.add(request)
    }
    private fun changeTemp(temp: Double): String {
        val changedTemp = (temp - 273.15)
        val df = DecimalFormat("#.#")
        df.roundingMode = RoundingMode.DOWN
        val roundoff = df.format(changedTemp)
        return roundoff
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
        // Android 6.0 Marshmallow 이상에서는 위치 권한에 추가 런타임 권한이 필요
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                true
            } else {
                // 권한이 없으므로 권한 요청 알림 보내기
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_PERMISSION_LOCATION)
                false
            }
        } else {
            true
        }
    }


}