package com.example.capstonecodinavi.Weather

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.capstonecodinavi.Main.MainActivity
import com.example.capstonecodinavi.R
import com.example.capstonecodinavi.User.UserActivity
import com.example.capstonecodinavi.databinding.ActivitySearchOthertimeBinding
import org.json.JSONException
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SearchOthertime : AppCompatActivity() {
    lateinit var binding: ActivitySearchOthertimeBinding
    lateinit var recyclerView: RecyclerView

    val nowTime = LocalDateTime.now()
    val formatedNowTime: String = nowTime.format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss"))
    var nextNum: Int = 0

    private var weatherInfoList: ArrayList<JSONObject> = ArrayList()
    private lateinit var weatherAdapter: WeatherAdapter

    companion object {
        var requestQueue: RequestQueue? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchOthertimeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        nextNum = intent.getIntExtra("nextNum", 0)
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(applicationContext)
        }

        setupRecyclerView()
        generateWeatherData()
        startWeatherRequest()
        action()
    }

    private fun setupRecyclerView() {
        weatherAdapter = WeatherAdapter(weatherInfoList)
        binding. recyclerView.layoutManager = GridLayoutManager(this, 3) // 한 줄에 3개의 아이템을 표시
        binding.recyclerView.adapter = weatherAdapter
    }

    private fun generateWeatherData() {
        // 현재 시간 기준으로부터 24시간 동안의 날씨 정보 생성
        val currentTime = LocalDateTime.now()

        // RecyclerView에 표시할 데이터 리스트 초기화
        weatherInfoList.clear()

        // 현재 시간부터 24시간 후까지 반복하여 날씨 정보 생성
        for (i in 0 until 24) {
            // 현재 시간으로부터 i시간 후의 시간을 계산
            val time = currentTime.plusHours(i.toLong())

            // JSON 객체 생성
            val weatherObject = JSONObject()
            weatherObject.put("time", time.format(DateTimeFormatter.ofPattern("HH:mm"))) // 시간 포맷 지정 필요
            weatherObject.put("weather", "맑음") // 날씨 정보 설정 (임시)
            // weatherObject.put("precipitationType", "없음") // 강수 형태 정보 설정 (임시)
            weatherObject.put("temp", "25") // 온도 정보 설정 (임시)

            // 생성한 JSON 객체를 리스트에 추가
            weatherInfoList.add(weatherObject)
        }

        // RecyclerView에 데이터가 변경되었음을 알림
        weatherAdapter.notifyDataSetChanged()
    }

    private fun startWeatherRequest() {
        val url = buildWeatherUrl()
        val request = StringRequest(
            com.android.volley.Request.Method.GET,
            url,
            Response.Listener { response -> handleWeatherResponse(response) },
            Response.ErrorListener { error -> handleWeatherError(error) }
        )
        request.setShouldCache(false)
        requestQueue?.add(request)
    }

    private fun buildWeatherUrl(): String {
        val baseUrl = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst"
        val serviceKey = "YOUR_SERVICE_KEY"
        val numOfRows = "1000"
        val pageNo = "1"
        val baseDate = nowTime.minusDays(nextNum.toLong()).format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val baseTime = "0500"
        val dataType = "JSON"
        val nx = "55"
        val ny = "127"
        return "$baseUrl?serviceKey=$serviceKey&numOfRows=$numOfRows&pageNo=$pageNo&base_date=$baseDate&base_time=$baseTime&dataType=$dataType&nx=$nx&ny=$ny"
    }

    private fun handleWeatherResponse(response: String) {
        try {
            val jsonObject = JSONObject(response)
            val responseObject = jsonObject.getJSONObject("response")
            val bodyObject = responseObject.getJSONObject("body")
            val itemsObject = bodyObject.getJSONObject("items")
            val itemArray = itemsObject.getJSONArray("item")

            // Clear existing data
            weatherInfoList.clear()

            for (i in 0 until itemArray.length()) {
                val item = itemArray.getJSONObject(i)
                weatherInfoList.add(item)
            }

            weatherAdapter.notifyDataSetChanged()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun handleWeatherError(error: VolleyError) {
        error.printStackTrace()
    }

    private fun action() {
        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.recyclerView.setOnClickListener {
            val intent = Intent(this, OthertimeActivity::class.java)
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
}
