package com.example.capstonecodinavi.Recommend

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.capstonecodinavi.Guide.IntroduceAppBtn
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
    }

    private fun action() {
        binding.backBtn.setOnClickListener {
            finish()
        }

        // '결혼식' 버튼 클릭 시
        binding.weddingBtn.setOnClickListener {
            openOccasionActivity("결혼식")
        }

        // '장례식' 버튼 클릭 시
        binding.funeralBtn.setOnClickListener {
            openOccasionActivity("장례식")
        }

        // '면접' 버튼 클릭 시
        binding.interviewBtn.setOnClickListener {
            openOccasionActivity("면접")
        }

        // '운동' 버튼 클릭 시
        binding.exerciseBtn.setOnClickListener {
            openOccasionActivity("운동")
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

    private fun openOccasionActivity(occasion: String){
        // SharedPreferences에서 데이터 조회
        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val clothingType = sharedPreferences.getString("CLOTHING_TYPE", "기본값")
        val clothingPattern = sharedPreferences.getString("CLOTHING_PATTERN", "기본값")
        val clothingColor = sharedPreferences.getString("CLOTHING_COLOR", "기본값")

        val intent = Intent(this, OccasionActivity::class.java).apply {
            putExtra("OCCASION", occasion)
            putExtra("CLOTHING_TYPE", clothingType)
            putExtra("CLOTHING_PATTERN", clothingPattern)
            putExtra("CLOTHING_COLOR", clothingColor)
        }
        startActivity(intent)
    }
}