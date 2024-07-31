package com.example.capstonecodinavi.Recommend

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.capstonecodinavi.Main.MainActivity
import com.example.capstonecodinavi.R
import com.example.capstonecodinavi.User.UserActivity
import com.example.capstonecodinavi.databinding.ActivityOccasionBinding

data class Clothing(val type: String?, val pattern: String?, val color: String?)

class OccasionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOccasionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOccasionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(" ")
        action()

        // 전달된 데이터 수신
        val occasion = intent.getStringExtra("OCCASION")
        val clothingType = intent.getStringExtra("CLOTHING_TYPE")
        val clothingPattern = intent.getStringExtra("CLOTHING_PATTERN")
        val clothingColor = intent.getStringExtra("CLOTHING_COLOR")

        val clothing = Clothing(clothingType, clothingPattern, clothingColor)

        // 적합성 검사 및 결과 메시지 표시
        val (isSuitable, suitabilityMessage) = isSuitableForOccasion(clothing, occasion)
        val guidelineMessage = getGuidelineForOccasion(occasion)
        displayMessages(suitabilityMessage, guidelineMessage)
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

    // 키워드별 적합성 검사 함수
    private fun isSuitableForOccasion(clothing: Clothing, occasion: String?): Pair<Boolean, String> {
        return when (occasion) {
            "결혼식" -> checkSuitability(
                clothing,
                unsuitableTypes = listOf("브라탑", "탑", "래깅스"),
                unsuitablePatterns = listOf("페이즐리", "뱀피", "해골", "카무플라쥬", "호피", "타이다이"),
                unsuitableColors = listOf("흰색"),
                occasionName = "결혼식"
            )
            "장례식" -> checkSuitability(
                clothing,
                suitableTypes = listOf("재킷", "스커트", "티셔츠", "셔츠", "팬츠"),
                suitablePatterns = listOf("무지"),
                suitableColors = listOf("검은색", "어두운 계열", "흰색"),
                occasionName = "장례식"
            )
            "면접" -> checkSuitability(
                clothing,
                suitableTypes = listOf("재킷", "스커트", "가디건", "티셔츠", "셔츠", "팬츠", "블라우스"),
                suitablePatterns = listOf("무지"),
                suitableColors = listOf("검은색", "흰색", "회색"),
                occasionName = "면접"
            )
            "운동" -> checkSuitability(
                clothing,
                suitableTypes = listOf("티셔츠", "팬츠", "브라탑", "탑", "후드티", "래깅스"),
                occasionName = "운동"
            )
            else -> Pair(false, "알 수 없는 상황입니다.")
        }
    }

    private fun checkSuitability(
        clothing: Clothing,
        suitableTypes: List<String>? = null,
        suitablePatterns: List<String>? = null,
        suitableColors: List<String>? = null,
        unsuitableTypes: List<String>? = null,
        unsuitablePatterns: List<String>? = null,
        unsuitableColors: List<String>? = null,
        occasionName: String
    ): Pair<Boolean, String> {
        val isSuitableType = suitableTypes?.contains(clothing.type) ?: true
        val isSuitablePattern = suitablePatterns?.contains(clothing.pattern) ?: true
        val isSuitableColor = suitableColors?.contains(clothing.color) ?: true

        val isUnsuitableType = unsuitableTypes?.contains(clothing.type) ?: false
        val isUnsuitablePattern = unsuitablePatterns?.contains(clothing.pattern) ?: false
        val isUnsuitableColor = unsuitableColors?.contains(clothing.color) ?: false

        val isSuitable = isSuitableType && isSuitablePattern && isSuitableColor &&
                !isUnsuitableType && !isUnsuitablePattern && !isUnsuitableColor

        return if (isSuitable) {
            Pair(true, "${clothing.color} ${clothing.pattern} ${clothing.type}은(는) $occasionName 옷차림에 적합합니다.")
        } else {
            Pair(false, "${clothing.color} ${clothing.pattern} ${clothing.type}은(는) $occasionName 옷차림에 적합하지 않습니다.")
        }
    }

    // 장소별 정형화된 옷차림 정보 함수
    private fun getGuidelineForOccasion(occasion: String?): String {
        return when (occasion) {
            "결혼식" -> "결혼식에서는 노출이 심한 옷, 너무 눈에 띄는 옷, 흰색 옷은 피해주세요."
            "장례식" -> "장례식에서는 되도록 검은색이나 어두운 계열 색의 옷을 권장합니다."
            "면접" -> "면접에서는 단정하고 깔끔한 옷차림을 권장합니다."
            "운동" -> "운동할 때는 편안하고 활동성이 좋은 옷을 입어주세요."
            else -> "알 수 없는 상황입니다."
        }
    }

    // 결과 메시지 및 장소별 정형화된 옷차림 정보를 표시하는 함수
    private fun displayMessages(suitabilityMessage: String, guidelineMessage: String) {
        binding.resultTv.text = suitabilityMessage
        binding.guidelineTv.text = guidelineMessage
    }
}
