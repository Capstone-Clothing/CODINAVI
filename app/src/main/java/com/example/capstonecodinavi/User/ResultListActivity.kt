package com.example.capstonecodinavi.User

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.capstonecodinavi.Main.MainActivity
import com.example.capstonecodinavi.R
import com.example.capstonecodinavi.databinding.ActivityResultListBinding
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class ResultListActivity : AppCompatActivity() {
    lateinit var binding: ActivityResultListBinding
    lateinit var adapter: ResultSummaryAdapter
    private lateinit var userId: String

    val resultSummaryList = ArrayList<ResultSummary>()

    companion object {
        var requestQueue: RequestQueue? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
        setTitle(" ")
        action()
        userId = intent.getStringExtra("userId")!!
        getResultFromDB(userId)

    }

    private fun initUI() {
        adapter = ResultSummaryAdapter(resultSummaryList)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = GridLayoutManager(this, 1)

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(applicationContext)
        }
    }

    private fun action() {
        binding.backBtn.setOnClickListener {
            finish()
        }

        adapter.itemClicked.observe(this) {
            val intent = Intent(this, RecommendResultActivity::class.java)
            intent.putExtra("id", adapter.itemClicked.value?.id)
            startActivity(intent)
        }

        binding.menuBottomNav.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.menu_home -> {
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

    fun getResultFromDB(userId: String) {
        val url = "http://3.34.34.170:8080/cloth/history/$userId"
        val request = object :
        StringRequest(
            Method.GET,
            url,
            Response.Listener { response ->
                try {
                    val jsonArray = JSONArray(response)

                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val id = jsonObject.get("id")
                        val color = jsonObject.get("color")
                        val pattern = jsonObject.get("pattern")
                        val type = jsonObject.get("type")

                        resultSummaryList.add(ResultSummary(id.toString(), color.toString(), pattern.toString(), type.toString()))
                    }
                    adapter.resultSummaryList = resultSummaryList
                    adapter.notifyDataSetChanged()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener {  }
        ) {}
        request.setShouldCache(false)
        requestQueue!!.add(request)
    }

}