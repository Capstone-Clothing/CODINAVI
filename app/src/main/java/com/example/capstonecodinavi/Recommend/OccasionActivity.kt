package com.example.capstonecodinavi.Recommend

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.capstonecodinavi.Main.MainActivity
import com.example.capstonecodinavi.R
import com.example.capstonecodinavi.User.UserActivity
import com.example.capstonecodinavi.databinding.ActivityOccasionBinding
import com.kakao.sdk.common.KakaoSdk.type

class OccasionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOccasionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOccasionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(" ")
        action()

        val occasion = intent.getStringExtra("occasion")
        val type = intent.getStringExtra("type")
        val pattern = intent.getStringExtra("pattern")
        val color = intent.getStringExtra("color")
        updateTextViewForOccasion(occasion, type, pattern, color)
    }

    private fun updateTextViewForOccasion(occasion: String?, type: String?, pattern: String?, color: String?) {
//        // 우선 임의 데이터 적용
//        val type = "셔츠" // 분석 결과에서 받은 옷 종류
//        val pattern = "무지" // 분석 결과에서 받은 옷 패턴
//        val color = "검정색" // 분석 결과에서 받은 옷 색상

        if (type == null || pattern == null || color == null) {
            binding.clothTv.text = "분석 결과를 받아오지 못했습니다."
            return
        }

        val analysisResult = if (isSuitableForOccasion(type, pattern, color, occasion)) {
            "이 옷은 ${occasion} 상황에 적합합니다."
        } else {
            "이 옷은 ${occasion} 상황에 적합하지 않습니다."
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
                val suitableTypes = listOf("재킷", "스커트", "티셔츠", "셔츠", "팬츠")
                val suitablePatterns = listOf("무지")
                val suitableColors = listOf("검은색", "짙은 회색", "짙은 남색", "짙은 갈색")
                val suitableWhiteTypes = listOf("티셔츠", "셔츠")
                (suitableTypes.contains(type) && suitablePatterns.contains(pattern) && (suitableColors.contains(color) || (color == "흰색" && suitableWhiteTypes.contains(type))))
            }
            "interview" -> {
                val suitableTypes = listOf("재킷", "스커트", "가디건", "티셔츠", "셔츠", "팬츠", "블라우스")
                val suitablePatterns = listOf("무지")
                val suitableColors = listOf("검은색", "회색", "남색", "흰색", "베이지")
                (suitableTypes.contains(type) && suitablePatterns.contains(pattern) && suitableColors.contains(color))
            }
            "exercise" -> {
                val suitableTypes = listOf("티셔츠", "팬츠", "브라탑", "탑", "후드티", "래깅스")
                suitableTypes.contains(type)
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