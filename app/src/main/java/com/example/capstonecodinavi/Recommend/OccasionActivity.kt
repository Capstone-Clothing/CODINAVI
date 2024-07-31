package com.example.capstonecodinavi.Recommend

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.capstonecodinavi.Main.MainActivity
import com.example.capstonecodinavi.R
import com.example.capstonecodinavi.User.UserActivity
import com.example.capstonecodinavi.databinding.ActivityOccasionBinding

class OccasionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOccasionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOccasionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(" ")
        action()

        val occasion = intent.getStringExtra("occasion")
        updateTextViewForOccasion(occasion)
    }

    private fun updateTextViewForOccasion(occasion: String?) {
        when (occasion) {
            "wedding" -> {
                // 결혼식에 적합한지 여부를 판별하는 코드를 호출하고 결과를 업데이트
                val analysisResult = "이 옷은 결혼식 상황에 적합하지 않습니다."
                binding.clothTv.text = analysisResult
            }
            "funeral" -> {
                // 장례식에 적합한지 여부를 판별하는 코드를 호출하고 결과를 업데이트
                val analysisResult = "이 옷은 장례식 상황에 적합하지 않습니다."
                binding.clothTv.text = analysisResult
            }
            "interview" -> {
                // 면접에 적합한지 여부를 판별하는 코드를 호출하고 결과를 업데이트
                val analysisResult = "이 옷은 면접 상황에 적합하지 않습니다."
                binding.clothTv.text = analysisResult
            }
            "exercise" -> {
                // 운동에 적합한지 여부를 판별하는 코드를 호출하고 결과를 업데이트
                val analysisResult = "이 옷은 운동 상황에 적합하지 않습니다."
                binding.clothTv.text = analysisResult
            }
            else -> {
                binding.clothTv.text = "알 수 없는 상황입니다."
            }
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
}