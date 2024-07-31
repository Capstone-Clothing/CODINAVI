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
        val type = "exampleType" // 분석 결과에서 받은 옷 종류
        val pattern = "examplePattern" // 분석 결과에서 받은 옷 패턴
        val color = "exampleColor" // 분석 결과에서 받은 옷 색상

        val analysisResult = if (isSuitableForOccasion(type, pattern, color, occasion)) {
            "이 옷은 ${occasion}에 적합합니다."
        } else {
            "이 옷은 ${occasion}에 적합하지 않습니다."
        }

        binding.clothTv.text = analysisResult
    }

    private fun isSuitableForOccasion(type: String, pattern: String, color: String, occasion: String?): Boolean {
        return when (occasion) {
            "wedding" -> {
                val unsuitableTypes = listOf("브라탑", "탑", "래깅스")
                val unsuitablePatterns = listOf("페이즐리", "뱀피", "해골", "카무플라쥬", "호피", "타이다이")
                val unsuitableColors = listOf("흰색")
                !(unsuitableTypes.contains(type) || unsuitablePatterns.contains(pattern) || unsuitableColors.contains(color))
            }
            "funeral" -> {
                val unsuitableTypes = listOf("브라탑", "탑", "래깅스")
                val unsuitablePatterns = listOf("페이즐리", "뱀피", "해골", "카무플라쥬", "호피", "타이다이")
                val unsuitableColors = listOf("빨간색")
                !(unsuitableTypes.contains(type) || unsuitablePatterns.contains(pattern) || unsuitableColors.contains(color))
            }
            "interview" -> {
                // 면접에 적합한지 여부를 판별하는 로직
                true // 예시로 true 반환
            }
            "exercise" -> {
                // 운동에 적합한지 여부를 판별하는 로직
                true // 예시로 true 반환
            }
            else -> true
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