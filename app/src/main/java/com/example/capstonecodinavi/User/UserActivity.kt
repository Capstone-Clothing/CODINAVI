package com.example.capstonecodinavi.User

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.capstonecodinavi.Guide.GuideActivity
import com.example.capstonecodinavi.Login.LoginActivity
import com.example.capstonecodinavi.Main.MainActivity
import com.example.capstonecodinavi.R
import com.example.capstonecodinavi.databinding.ActivityUserBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.kakao.sdk.user.UserApiClient

class UserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserBinding
    private lateinit var sharedPreferences: SharedPreferences

    // SharedPreferences에 사용자의 로그인 방법을 저장하는 키
    companion object {
        const val LOGIN_METHOD_KEY = "loginMethod"
    }

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

        // SharedPreferences에서 로그인 방법 가져오기
        val loginMethod = sharedPreferences.getString("loginMethod", null)
        if (loginMethod != null) {
            Log.d("[Login Method]", "Login Method: $loginMethod")
        } else {
            Log.e("[Login Method]", "Login Method is null")
        }
    }

    private fun action() {
        binding.genderBtn.setOnClickListener {
            val intent = Intent(this, GenderActivity::class.java)
            startActivity(intent)
        }

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

        binding.menuBottomNav.setOnItemSelectedListener { menuItem->
            when(menuItem.itemId) {
                R.id.menu_home -> {
                    // 홈 버튼 클릭 시 MainActivity로 이동
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menu_user -> {
                    true
                }
                else -> false
            }
        }
    }

    private fun moveToLoginScreen() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    // 로그아웃
    private fun logout() {
        Log.d("[로그아웃]", "로그아웃 버튼 클릭됨")

        // 전달된 로그인 방법 값을 가져옴
        val loginMethod = sharedPreferences.getString(LOGIN_METHOD_KEY, null)

        when (loginMethod) {
            "google" -> {
                Log.d("[구글 로그아웃]", "구글 로그아웃 수행")

                // 구글 로그아웃 수행
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build()
                val googleSignInClient = GoogleSignIn.getClient(this, gso)
                googleSignInClient.signOut()

                Log.d("[구글 로그아웃]", "구글 로그아웃 완료")

                // SharedPreferences에서 로그인 상태 변경
                val editor = sharedPreferences.edit()
                editor.putBoolean("isLoggedIn", false)
                editor.apply()

                Log.d("[구글 로그아웃]", "로그인 상태 변경 완료")

                // 로그인 화면으로 이동
                moveToLoginScreen()
            }
            "kakao" -> {
                Log.d("[카카오 로그아웃]", "카카오 로그아웃 수행")

                // 카카오 로그아웃 수행
                UserApiClient.instance.logout { error ->
                    if (error != null) {
                        // 로그아웃 실패 시
                        Log.e("[카카오 로그아웃]", "카카오 로그아웃 실패", error)
                    } else {
                        // 로그아웃 성공 시
                        Log.i("[카카오 로그아웃]", "카카오 로그아웃 성공")

                        // SharedPreferences에서 로그인 상태 변경
                        val editor = sharedPreferences.edit()
                        editor.putBoolean("isLoggedIn", false)
                        editor.apply()

                        Log.d("[카카오 로그아웃]", "로그인 상태 변경 완료")

                        // 로그인 화면으로 이동
                        moveToLoginScreen()
                    }
                }
            }
            else -> {
                Log.e("[로그아웃]", "로그인 방법이 잘못되었습니다.")
                moveToLoginScreen()
            }
        }
    }

}