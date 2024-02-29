package com.example.capstonecodinavi


import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.capstonecodinavi.databinding.ActivityAdminBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class AdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminBinding
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
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