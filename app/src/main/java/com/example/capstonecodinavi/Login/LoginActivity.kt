package com.example.capstonecodinavi.Login

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.capstonecodinavi.BuildConfig
import com.example.capstonecodinavi.Guide.IntroduceAppBtn
import com.example.capstonecodinavi.Main.MainActivity
import com.example.capstonecodinavi.R
import com.example.capstonecodinavi.databinding.ActivityLoginBinding
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.network.origin
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.User


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences
    // 카카오 SDK에서 사용되는 로그인 결과 처리를 위한 콜백 함수를 저장하는 변수
    private lateinit var kakaoCallback: (OAuthToken?, Throwable?) -> Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(" ")

        initSharedPreferences()
        initKakaoSdk()

        // 사용자가 이미 로그인 한 경우 MainActivity로 이동
        if(isLoggedIn()) {
            moveToMainScreen()
            return
        } else {
            setupLoginButton()
            setKakaoCallback()  // KakaoCallback 설정
        }
    }

    // SharedPreferences 초기화
    private fun initSharedPreferences() {
        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE)
    }

    // Kakao SDK 초기화
    private fun initKakaoSdk() {
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
    }

    // 카카오 로그인 버튼 설정
    private fun setupLoginButton() {
        val kakaoLoginButton = findViewById<Button>(R.id.kakaoLoginBtn)
        kakaoLoginButton.setOnClickListener { btnKakaoLogin() }
    }

    // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
    private fun btnKakaoLogin() {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            UserApiClient.instance.loginWithKakaoTalk(this, callback = kakaoCallback)
        } else {
            UserApiClient.instance.loginWithKakaoAccount(this, callback = kakaoCallback)
        }
    }

    private fun setKakaoCallback() {
        kakaoCallback = { token, error ->
            when {
                error != null -> handleLoginError(error)
                token != null -> handleLoginSuccess(token)
                else -> Log.d("[카카오 로그인]", "토큰 == null error == null")
            }
        }
    }

    private fun handleLoginError(error: Throwable) {
        val errorMessage = when (error.toString()) {
            AuthErrorCause.AccessDenied.toString() -> "접근이 거부됨(동의 취소)"
            AuthErrorCause.InvalidClient.toString() -> "유효하지 않은 앱"
            AuthErrorCause.InvalidGrant.toString() -> "인증 수단이 유효하지 않아 인증할 수 없는 상태"
            AuthErrorCause.InvalidRequest.toString() -> "요청 파라미터 오류"
            AuthErrorCause.InvalidScope.toString() -> "유효하지 않은 scope ID"
            AuthErrorCause.Misconfigured.toString() -> "설정이 올바르지 않음(android key hash)"
            AuthErrorCause.ServerError.toString() -> "서버 내부 에러"
            AuthErrorCause.Unauthorized.toString() -> "앱이 요청 권한이 없음"
            else -> "기타 에러"
        }
        Log.d("[카카오 로그인]", errorMessage)
        Log.d("[카카오 로그인]", error.toString())
    }

    private fun handleLoginSuccess(token: OAuthToken) {
        Log.d("[카카오 로그인]", "로그인에 성공하였습니다.\n${token.accessToken}")
        getUserInfo()
        saveLoginStatus(true)
        saveLoginMethod("kakao")
        checkAndMoveToNextScreen()
    }

    // 카카오 로그인 후 사용자 정보 가져오기
    private fun getUserInfo() {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e(TAG, "사용자 정보 요청 실패", error)
            } else if (user != null) {
                val userName = user.kakaoAccount?.profile?.nickname ?: "Unknown"
                Log.i("[카카오 로그인]", "사용자 정보 : ID = ${user.id}, 이름 = $userName")
                saveUsername(userName)
            }
        }
    }

    // 로그인 방법 저장
    private fun saveLoginMethod(loginMethod: String) {
        with(sharedPreferences.edit()) {
            putString("loginMethod", loginMethod)
            apply()
        }
    }

    // MainActivity로 이동
    private fun moveToMainScreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // 현재 Activity 종료
    }

    // MainActivity 또는 IntroduceAppBtn으로 이동
    private fun checkAndMoveToNextScreen() {
        val sharedPref = getSharedPreferences("app_preferences", MODE_PRIVATE)
        val hasShownIntroduce = sharedPref.getBoolean("hasShownIntroduceAppBtn", false)

        if (!hasShownIntroduce) {
            // IntroduceAppBtn 액티비티를 한 번도 보여준 적이 없는 경우
            with(sharedPref.edit()) {
                putBoolean("hasShownIntroduceAppBtn", true)
                apply()
            }

            val intent = Intent(this, IntroduceAppBtn::class.java)
            startActivity(intent)
        } else {
            // MainActivity로 이동
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        finish()
    }

    // 로그인 상태 저장
    private fun saveLoginStatus(isLoggedIn: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean("isLoggedIn", isLoggedIn)
            apply()
        }
    }

    // 로그인 상태 가져오기
    private fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // 뒤로가기 누르면 앱 종료
        finishAffinity()
    }

    // 사용자 이름 저장
    private fun saveUsername(username: String) {
        with(sharedPreferences.edit()) {
            putString("username", username)
            apply()
        }
    }
}