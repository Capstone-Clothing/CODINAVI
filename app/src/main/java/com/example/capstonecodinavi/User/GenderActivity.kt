package com.example.capstonecodinavi.User

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import com.example.capstonecodinavi.Main.MainActivity
import com.example.capstonecodinavi.R
import com.example.capstonecodinavi.databinding.ActivityGenderBinding

class GenderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGenderBinding
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGenderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // sharedPreferences 초기화
        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE)

        // 저장된 성별 정보 가져오기
        val gender = sharedPreferences.getInt("gender", -1)
        Log.d("gender", gender.toString())

        // 저장된 성별이 있으면 해당 라디오 버튼 체크
        if (gender != -1) {
            val btn = binding.radioGroup.getChildAt(gender) as? RadioButton
            btn?.isChecked = true
            updateGenderTV(gender)    // TextView 업데이트
        }

        setTitle(" ")
        action()
    }

    private fun action() {
        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.genderChangeBtn.setOnClickListener {
            val index: Int = binding.radioGroup.indexOfChild(findViewById(binding.radioGroup.checkedRadioButtonId))
            saveGender(index)
            updateGenderTV(index)   // TextView 업데이트

            if (index == 0) {   // index가 0이면 남성
                Toast.makeText(this, "성별을 남성으로 설정하였습니다.", Toast.LENGTH_SHORT).show()
            } else if (index == 1) {    // index가 1이면 여성
                Toast.makeText(this, "성별을 여성으로 설정하였습니다.", Toast.LENGTH_SHORT).show()
            }

            // Gender 설정 후 MainActivity로 이동
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
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

    // 성별 정보 저장 함수
    private fun saveGender(gender: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("gender", gender)
        editor.apply()
    }

    // TextView 업데이트 함수
    private fun updateGenderTV(gender: Int) {
        val genderText = when(gender) {
            0 -> "현재 성별: 남성"   // 0번 라디오 버튼이 남성인 경우
            1 -> "현재 성별: 여성"   // 1번 라디오 버튼이 남성인 경우
            else -> "설정해주세요"
        }
        binding.gender.text = genderText
    }
}