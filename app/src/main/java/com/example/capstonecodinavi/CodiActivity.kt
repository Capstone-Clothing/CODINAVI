package com.example.capstonecodinavi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.capstonecodinavi.databinding.ActivityCodiBinding
import org.json.JSONException
import org.json.JSONObject

class CodiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCodiBinding
    companion object {
        var requestQueue: RequestQueue? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCodiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(" ")
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(applicationContext)
        }
        testApi()
        action()
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

    fun testApi() {
        val url = "http://1.231.55.229:8080/clothInfo?name=123123123123123123"
        val request = object :
            StringRequest(
                Method.GET,
                url,
                Response.Listener { response ->
                    try {
                        Log.d("APITEST","${response}")
                        val jsonobject = JSONObject(response)
                        val name = jsonobject.getString("name")
                        binding.textView.text = name

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { }
            ) { }
        request.setShouldCache(false) // 이전 결과가 있어도 새 요청하여 결과 보여주기
        requestQueue!!.add(request)
    }

}