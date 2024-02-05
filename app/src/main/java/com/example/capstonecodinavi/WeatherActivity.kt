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
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.*
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date

class WeatherActivity : AppCompatActivity() {
    companion object {
        var requestQueue: RequestQueue? = null
    }
    lateinit var binding: ActivityWeatherBinding
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null // 현재 위치를 가져오기 위한 변수
    lateinit var mLastLocation: Location // 위치(위도, 경도) 값을 가지고 있는 객체
    internal lateinit var mLocationRequest: LocationRequest
    private val REQUEST_PERMISSION_LOCATION = 10
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        action()
        setting()
    }
    private fun setting() {
        mLocationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(applicationContext)
        }
    }
    private fun CurrentWeatherCall() {
        val url = "https://api.openweathermap.org/data/2.5/weather?lat=${mLastLocation.latitude}&lon=${mLastLocation.longitude}&appid=2d360c1fe9d2bade8fc08a1679683e24"
        val request = object : StringRequest(
            Request.Method.GET,
            url,
            Response.Listener { response ->
                try {
                    // JSON 데이터 가져오기
                    val jsonObject = JSONObject(response)
                    val weatherJson = jsonObject.getJSONArray("weather")
                    val weatherObj = weatherJson.getJSONObject(0)
                    val weather = weatherObj.getString("main")
                    if (weather.contains("Rain")) binding.weatherView!!.text = "비"
                    else if (weather.contains("Snow")) binding.weatherView!!.text = "눈"
                    else if (weather.contains("Clouds")) binding.weatherView!!.text = "흐림"
                    else binding.weatherView!!.text = "맑음"
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { }) { }
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
                startLocationUpdates()
            } else {
                Log.d("ttt", "onRequestPermissionResult() _ 권한 허용 거부")
                Toast.makeText(this, "권한이 없어 해당 기능을 실행할 수 없습니다", Toast.LENGTH_SHORT).show()
            }
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

        binding.currentStateBtn.setOnClickListener {
            // https://developer.android.com/training/location/permissions?hl=ko => 현재 위치를 가져오기 위한 api 문서
            // 나중에는 위도와 경도의 값만 필요하니까 이 콜백함수 지우고 밑에 코드만 action() 밑이나 위에 놔두면 됨.
            if(checkPermissionForLocation(this)) {
                startLocationUpdates()
            }
        }
        binding.Button.setOnClickListener {
            val now = System.currentTimeMillis()
            val date = Date(now)
            val simpleDateFormatDay = SimpleDateFormat("MM")
            val getMonth = simpleDateFormatDay.format(date)
            val getDate = """
                $getMonth
            """.trimIndent()

            CurrentWeatherCall()

            if (getMonth == "12" || getMonth == "01" || getMonth == "02") binding.dateView.text = "겨울"
            else if (getMonth == "03" || getMonth == "04" || getMonth == "05") binding.dateView.text = "봄"
            else if (getMonth == "06" || getMonth == "07" || getMonth == "08") binding.dateView.text = "여름"
            else binding.dateView.text = "가을"
        }
    }

    private val mLocationCallback = object: LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            // 시스템에서 받은 location 정보를 onLocationChanged()에 전달
            onLocationChanged(locationResult.lastLocation)
        }
    }
    private fun startLocationUpdates() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        // 기기의 위치에 관한 정기 업데이트를 요청하는 메서드 실행
        // 지정한 루퍼 스레드(Looper.myLooper())에서 콜백(mLocationCallback)으로 위치 업데이트를 요청
        mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
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
    fun onLocationChanged(location: Location) {
        mLastLocation = location
        binding.text1.setText("위도 : " + mLastLocation.latitude)
        binding.text2.setText("경도 : " + mLastLocation.longitude)
    }
}