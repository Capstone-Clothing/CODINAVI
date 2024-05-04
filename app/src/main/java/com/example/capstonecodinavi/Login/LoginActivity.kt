package com.example.capstonecodinavi.Login

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.capstonecodinavi.BuildConfig
import com.example.capstonecodinavi.Main.MainActivity
import com.example.capstonecodinavi.R
import com.example.capstonecodinavi.User.UserActivity
import com.example.capstonecodinavi.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.User


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(" ")

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE)

        // Kakao SDK 초기화
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)

        // 사용자가 이미 로그인 한 경우 MainActivity로 이동
        if(isLoggedIn()) {
            moveToMainScreen()
            return
        }

        // 구글 로그인 옵션 설정
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()     // 이메일 요청
            .build()

        // 구글 로그인 클라이언트 설정
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        // 구글 로그인 버튼 설정
        val googleLoginButton = findViewById<SignInButton>(R.id.googleLoginBtn)
        googleLoginButton.setOnClickListener {
            signIn()
        }

        // 카카오 로그인 버튼 설정
        val kakaoLoginButton = findViewById<Button>(R.id.kakaoLoginBtn)
        kakaoLoginButton.setOnClickListener {
            btnKakaoLogin()
        }

        // KakaoCallback 설정
        setKakaoCallback()

    }

    // 구글 로그인 요청 시작
    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        try {
            startActivityForResult(signInIntent, RC_SIGN_IN)
        } catch (e: Exception) {
            // 예외 처리 코드 추가
            Log.e("LoginActivity", "Error starting sign-in activity: ${e.message}", e)
        }
    }

    // 구글 로그인 결과 처리
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val username = account?.displayName

                saveUsername(username ?: "Unknown") // 사용자 이름이 없을 경우
                saveLoginStatus(true)   // 로그인 상태 저장

                val loginMethod = "google" // 구글 로그인 방법 저장
                saveLoginMethod(loginMethod) // SharedPreferences에 저장

                moveToMainScreen()  // MainActivity로 이동
            } catch (e: ApiException) {
                // Google Sign In 실패 처리
                Log.e("LoginActivity", "Google sign in failed: ${e.statusCode}")
            }
        }
    }

    // 카카오 SDK에서 사용되는 로그인 결과 처리를 위한 콜백 함수를 저장하는 변수
    lateinit var kakaoCallback: (OAuthToken?, Throwable?) -> Unit

    private fun btnKakaoLogin() {
        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            UserApiClient.instance.loginWithKakaoTalk(this, callback = kakaoCallback)
        } else {
            UserApiClient.instance.loginWithKakaoAccount(this, callback = kakaoCallback)
        }
    }

    private fun setKakaoCallback() {
        kakaoCallback = { token, error ->
            if (error != null) {
                when {
                    error.toString() == AuthErrorCause.AccessDenied.toString() -> {
                        Log.d("[카카오로그인]", "접근이 거부됨(동의 취소)")
                    }
                    error.toString() == AuthErrorCause.InvalidClient.toString() -> {
                        Log.d("[카카오로그인]", "유효하지 않은 앱")
                    }
                    error.toString() == AuthErrorCause.InvalidGrant.toString() -> {
                        Log.d("[카카오로그인]", "인증 수단이 유효하지 않아 인증할 수 없는 상태")
                    }
                    error.toString() == AuthErrorCause.InvalidRequest.toString() -> {
                        Log.d("[카카오로그인]", "요청 파라미터 오류")
                    }
                    error.toString() == AuthErrorCause.InvalidScope.toString() -> {
                        Log.d("[카카오로그인]", "유효하지 않은 scope ID")
                    }
                    error.toString() == AuthErrorCause.Misconfigured.toString() -> {
                        Log.d("[카카오로그인]", "설정이 올바르지 않음(android key hash)")
                    }
                    error.toString() == AuthErrorCause.ServerError.toString() -> {
                        Log.d("[카카오로그인]", "서버 내부 에러")
                    }
                    error.toString() == AuthErrorCause.Unauthorized.toString() -> {
                        Log.d("[카카오로그인]", "앱이 요청 권한이 없음")
                    }
                    else -> { // Unknown
                        Log.d("[카카오로그인]",error.toString())
                        Log.d("[카카오로그인]", "기타 에러")
                    }
                }
            } else if (token != null){
                // 카카오 로그인 성공 시 처리
                Log.d("[카카오로그인]", "로그인에 성공하였습니다.\n${token.accessToken}")
                getUserInfo() // 사용자 정보 가져오기

                saveLoginStatus(true)   // 로그인 상태 저장
                saveLoginMethod("kakao")   // 카카오 로그인 방법 저장

                moveToMainScreen()  // MainActivity로 이동
            } else {
                Log.d("카카오 로그인", "토큰 == null error == null")
            }
        }
    }

    // 카카오 로그인 후 사용자 정보 가져오기
    private fun getUserInfo() {
        // 사용자 정보 요청
        UserApiClient.instance.me { user: User?, error: Throwable? ->
            if (error != null) {
                Log.e(TAG, "사용자 정보 요청 실패", error)
            } else if (user != null) {
                // 사용자 정보 가져오기 성공
                val userId = user.id.toString()
                val userName = user.kakaoAccount?.profile?.nickname ?: "Unknown"
                Log.i("[카카오 로그인]", "사용자 정보 : ID = $userId, 이름 = $userName")
                saveUsername(userName)  // 사용자 이름 저장
            }
        }
    }

    // 로그인 방법 저장
    private fun saveLoginMethod(loginMethod: String) {
        val editor = sharedPreferences.edit()
        editor.putString("loginMethod", loginMethod)
        editor.apply()
    }

    // MainActivity로 이동
    private fun moveToMainScreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // 현재 Activity 종료
    }

    // 로그인 상태 저장
    private fun saveLoginStatus(isLoggedIn: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.apply()
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
        val editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.apply()
    }

    companion object {
        const val RC_SIGN_IN = 123
    }
}