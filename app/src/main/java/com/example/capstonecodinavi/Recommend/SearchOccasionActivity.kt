package com.example.capstonecodinavi.Recommend

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.capstonecodinavi.Camera.CameraActivity
import com.example.capstonecodinavi.Main.MainActivity
import com.example.capstonecodinavi.R
import com.example.capstonecodinavi.User.UserActivity
import com.example.capstonecodinavi.databinding.ActivitySearchOccasionBinding

class SearchOccasionActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchOccasionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchOccasionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(" ")
        action()

        // 버튼 클릭 리스너 설정
        binding.weddingBtn.setOnClickListener {
            openOccasionActivity("wedding")
        }
        binding.funeralBtn.setOnClickListener {
            openOccasionActivity("funeral")
        }
        binding.interviewBtn.setOnClickListener {
            openOccasionActivity("interview")
        }
        binding.exerciseBtn.setOnClickListener {
            openOccasionActivity("exercise")
        }
    }

    private fun openOccasionActivity(occasion: String) {
        val intent = Intent(this, OccasionActivity::class.java).apply {
            putExtra("occasion", occasion)
        }
        startActivity(intent)
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
}