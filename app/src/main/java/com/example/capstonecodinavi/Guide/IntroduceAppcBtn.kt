package com.example.capstonecodinavi.Guide

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.capstonecodinavi.Main.MainActivity
import com.example.capstonecodinavi.R
import com.example.capstonecodinavi.User.UserActivity
import com.example.capstonecodinavi.databinding.ActivityIntroduceAppcBtnBinding

class IntroduceAppcBtn : AppCompatActivity() {
    private lateinit var binding: ActivityIntroduceAppcBtnBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroduceAppcBtnBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(" ")
        action()
    }

    private fun action() {
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

        val textView = findViewById<TextView>(R.id.startTVBtn) // XML에서 정의한 TextView를 가져옵니다.
        val content = textView.text.toString() // TextView의 문자열 내용을 가져옵니다.
        val spannableString = SpannableString(content) // SpannableString을 생성합니다.
        spannableString.setSpan(UnderlineSpan(), 0, spannableString.length, 0) // SpannableString에 밑줄을 추가합니다.
        textView.text = spannableString // TextView에 SpannableString을 설정하여 밑줄이 추가된 텍스트를 표시합니다.

        textView.setOnClickListener {
            moveToMainScreen()
        }
    }

    // MainActivity로 이동
    private fun moveToMainScreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // 현재 Activity 종료
    }
}