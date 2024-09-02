package com.example.capstonecodinavi.User

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.capstonecodinavi.Camera.CameraActivity
import com.example.capstonecodinavi.Main.MainActivity
import com.example.capstonecodinavi.R
import com.example.capstonecodinavi.databinding.ActivityRecommendResultBinding
import org.json.JSONException
import org.json.JSONObject
import java.lang.RuntimeException

class RecommendResultActivity : AppCompatActivity() {
    lateinit var binding: ActivityRecommendResultBinding

    companion object {
        var requestQueue: RequestQueue? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecommendResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(" ")

        initUI()
        action()

        getRecommendClothHistory(intent.getStringExtra("id")!!)
        getRecommendColorHistory(intent.getStringExtra("id")!!)
        Log.d("intentValue", intent.getStringExtra("id")!!)
    }

    private fun initUI() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(applicationContext)
        }
    }

    private fun action() {
        binding.backBtn.setOnClickListener {
            finish()
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

    fun getRecommendClothHistory(id: String) {
        val url = "http://3.34.34.170:8080/cloth/recommendHistory/$id"

        val request = object :
            StringRequest(
                Method.GET,
                url,
                Response.Listener { response ->
                    try {
                        val jsonObject = JSONObject(response)
                        binding.codiResultTv.text = jsonObject.getString("result")

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener {  }
            ) {}
        request.setShouldCache(false)
        requestQueue!!.add(request)
    }

    fun getRecommendColorHistory(id: String) {
        val url = "http://3.34.34.170:8080/cloth/colorRecommendHistory/$id"

        val request = object :
            StringRequest(
                Method.GET,
                url,
                Response.Listener { response ->
                    try {
                        val jsonObject = JSONObject(response)
                        binding.colorResultTv.text = jsonObject.getString("result")

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