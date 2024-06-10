package com.example.capstonecodinavi.User

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.capstonecodinavi.Guide.IntroduceAppBtn
import com.example.capstonecodinavi.databinding.ActivityFirstGenderSetBinding

class FirstGenderSetActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFirstGenderSetBinding
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirstGenderSetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // sharedPreferences 초기화
        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE)

        setTitle(" ")
        action()
    }

    private fun action() {
        binding.genderSetBtn.setOnClickListener {
            val selectedRadioButtonId = binding.radioGroup.checkedRadioButtonId
            if (selectedRadioButtonId != -1) {
                val index = binding.radioGroup.indexOfChild(findViewById(selectedRadioButtonId))
                saveGender(index)

                if (index == 0) {   // index가 0이면 남성
                    Toast.makeText(this, "성별을 남성으로 설정하였습니다.", Toast.LENGTH_SHORT).show()
                } else if (index == 1) {    // index가 1이면 여성
                    Toast.makeText(this, "성별을 여성으로 설정하였습니다.", Toast.LENGTH_SHORT).show()
                }

                // Gender 설정 후 IntroduceAppBtnActivity로 이동
                val intent = Intent(this, IntroduceAppBtn::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "성별을 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 성별 정보 저장 함수
    private fun saveGender(gender: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("gender", gender)
        editor.apply()

        // GenderActivity로 성별 정보를 전달
        val genderIntent = Intent(this, GenderActivity::class.java)
        genderIntent.putExtra("gender", gender)
        startActivity(genderIntent)
    }
}