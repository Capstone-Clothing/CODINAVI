package com.example.capstonecodinavi.Main

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.capstonecodinavi.Camera.CameraActivity
import com.example.capstonecodinavi.R
import com.example.capstonecodinavi.User.FirstGenderSetActivity
import com.example.capstonecodinavi.User.UserActivity
import com.example.capstonecodinavi.Weather.WeatherActivity
import com.example.capstonecodinavi.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences
    val REQUEST_IMAGE_CAPTURE = 1
    companion object {
        var imageBitmap: Bitmap? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // sharedPreferences 초기화
        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE)

        // 저장된 성별 정보 가져오기
        val gender = sharedPreferences.getInt("gender", -1)

        // 성별이 설정되지 않은 경우 FirstGenderSetActivity로 이동
        if (gender == -1) {
            val intent = Intent(this, FirstGenderSetActivity::class.java)
            startActivity(intent)
            finish()
        }

        setTitle(" ")
        action()
    }

    private fun action() {
        binding.cameraBtn.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }

        binding.weatherBtn.setOnClickListener {
            val intent = Intent(this, WeatherActivity::class.java)
            startActivity(intent)
        }

        binding.menuBottomNav.setOnItemSelectedListener { menuItem->
            when(menuItem.itemId) {
                R.id.menu_home -> {
                    true
                }
                R.id.menu_user -> {
                    // 마이페이지 버튼 클릭 시 UserActivity로 이동
                    val intent = Intent(this, UserActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        // 현재 태스크를 백그라운드로 이동시킴
        moveTaskToBack(true)
    }
}