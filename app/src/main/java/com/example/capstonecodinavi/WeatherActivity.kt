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
    private var address = ArrayList<String>()
    private var season: String? = null
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
                    address = getAddress(lat!!, lon!!)!!

                    //Log.d("checkcheck2","${address!!.adminArea} ${address!!.locality} ${address!!.thoroughfare}")
                    Log.d("hihi", "$address")
                    Log.d("checkcheck","${address!!.get(1)} ${address!!.get(2)} ${address!!.get(3)}")
                    getCurrentWeather()
                    getCurrentSeason()
                }
            }
    }
    private fun getAddress(lat: Double, lon: Double): ArrayList<String>? {
        lateinit var address: ArrayList<String>

        try {
            val geocoder = Geocoder(this, Locale.KOREA)
            val addrList = geocoder.getFromLocation(lat, lon, 1)

            if (addrList != null) {
                for (addr in addrList) {
                    val spliteAddr = addr.getAddressLine(0).split(" ")
                    address = spliteAddr as ArrayList<String>
                }
            }
            return address
        } catch (e: IOException) {
            Toast.makeText(this, "주소를 가져올 수 없습니다",Toast.LENGTH_SHORT).show()
            return null
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

                        var newAddrList = ArrayList<String>()
                        var i= 0
                        Log.d("addrcheck","$address")
                        for(addr in address!!) {
                            if(addr == "서울특별시" || addr.endsWith("도")) {
                                newAddrList!!.add(0, addr) //0
                                i++
                                if(addr != "서울특별시" && addr.endsWith("시")) {
                                    newAddrList!!.add(1, addr) //1
                                    i++
                                    if(addr.endsWith("군")) {
                                        newAddrList!!.add(2, addr) //2
                                        i++
                                        if(addr.endsWith("읍")) {
                                            newAddrList!!.add(3, addr) //3
                                            i++
                                            if (addr.endsWith("리")) {
                                                newAddrList!!.add(4, addr) //4
                                            }
                                        }
                                        else if (addr.endsWith("면")) {
                                            newAddrList!!.add(3, addr) //3
                                            i++
                                            if (addr.endsWith("리")) {
                                                newAddrList!!.add(4, addr) //4
                                            }
                                        }
                                        else if (addr.endsWith("리")) {
                                            newAddrList!!.add(3, addr) //3
                                        }
                                    }
                                    else if(addr.endsWith("구")) {
                                        newAddrList!!.add(2, addr) //2
                                        i++
                                        if(addr.endsWith("읍")) {
                                            newAddrList!!.add(3, addr) //3
                                            i++
                                            if(addr.endsWith("리")) {
                                                newAddrList!!.add(4, addr) //4
                                            }
                                        }
                                        else if (addr.endsWith("면")) {
                                            newAddrList!!.add(3, addr) //3
                                            i++
                                            if(addr.endsWith("리")) {
                                                newAddrList!!.add(4, addr) //4
                                            }
                                        }
                                        else if (addr.endsWith("동")) {
                                            newAddrList!!.add(3, addr) //3
                                        }
                                    }
                                    else {
                                        if(addr.endsWith("읍")) {
                                            newAddrList!!.add(2, addr) //2
                                            i++
                                            if(addr.endsWith("리")) {
                                                newAddrList!!.add(3, addr) //3
                                            }
                                        }
                                        else if (addr.endsWith("면")) {
                                            newAddrList!!.add(2, addr) //2
                                            i++
                                            if(addr.endsWith("리")) {
                                                newAddrList!!.add(3, addr) //3
                                            }
                                        }
                                    }
                                }
                            }
                            else {
                                Toast.makeText(this,"현재 위치의 주소가 없습니다.",Toast.LENGTH_SHORT).show()
                            }
                        }
                        binding.instructionTv.text = "현재 위치는 ${newAddrList!!.get(newAddrList.size-(newAddrList.size))} ${newAddrList!!.get(newAddrList.size-(newAddrList.size-1))} ${newAddrList!!.get(newAddrList.size-(newAddrList.size-2))}입니다. \n계절은 ${season}이고 날씨는 ${weatherStr}이며 기온은 ${celsius}도 입니다."
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