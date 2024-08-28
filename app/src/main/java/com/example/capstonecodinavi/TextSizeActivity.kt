package com.example.capstonecodinavi

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.capstonecodinavi.Main.MainActivity
import com.example.capstonecodinavi.User.UserActivity
import com.example.capstonecodinavi.databinding.ActivityTextSizeBinding

class TextSizeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTextSizeBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTextSizeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(" ")

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("textSizePrefs", MODE_PRIVATE)
        val savedTextSize = sharedPreferences.getFloat("textSize", 35f) // 기본값: 35sp
        binding.codinavi.textSize = savedTextSize

        // 버튼 클릭 리스너 설정
        binding.midBtn.setOnClickListener {
            updateTextSize(35f) // 보통 크기 (35sp)
        }

        binding.bigBtn.setOnClickListener {
            updateTextSize(45f) // 큰 크기 (45sp)
        }

        binding.bigbigButton.setOnClickListener {
            updateTextSize(55f) // 매우 큰 크기 (55sp)
        }

        action()
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

    private fun updateTextSize(size: Float) {
        binding.codinavi.textSize = size
        sharedPreferences.edit().putFloat("textSize", size).apply()
    }
}