package com.example.capstonecodinavi

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.capstonecodinavi.databinding.ActivityUserBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class UserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserBinding
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 초기화
        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE)

        // 저장된 사용자 이름 가져오기
        val username = sharedPreferences.getString("username", "")

        // 사용자 이름이 비어있는 경우 "Unknown"을 사용
        val displayedName = if (username.isNullOrEmpty()) "Unknown" else username

        // TextView에 사용자 이름 설정
        binding.username.text = displayedName

        setTitle(" ")
        action()
    }

    private fun action() {
        binding.homeBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.profileBtn.setOnClickListener {
            val intent = Intent(this, UserActivity::class.java)
            startActivity(intent)
        }

//        binding.genderBtn.setOnClickListener {
//          //성별 바꾸는 화면으로 이동
//        }

        binding.guideBtn.setOnClickListener {
            val intent = Intent(this, GuideActivity::class.java)
            startActivity(intent)
        }

//        binding.administratorBtn.setOnClickListener {
//          //관리자 문의 화면으로 이동
//        }

        binding.logoutBtn.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        // Google 로그인 해제
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut()

        // SharedPreferences에서 로그인 상태 변경
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", false)
        editor.apply()

        // 로그인 화면으로 이동
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}