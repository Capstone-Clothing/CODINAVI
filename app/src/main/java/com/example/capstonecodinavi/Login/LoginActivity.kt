package com.example.capstonecodinavi.Login

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.capstonecodinavi.Main.MainActivity
import com.example.capstonecodinavi.R
import com.example.capstonecodinavi.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(" ")
        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE)

        if(isLoggedIn()) {
            moveToMainScreen()
            return
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()     // 이메일 요청
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val googleLoginButton = findViewById<SignInButton>(R.id.googleLoginBtn)
        googleLoginButton.setOnClickListener {
            signIn()
        }
    }
    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        try {
            startActivityForResult(signInIntent, RC_SIGN_IN)
        } catch (e: Exception) {
            // 예외 처리 코드 추가
            Log.e("LoginActivity", "Error starting sign-in activity: ${e.message}", e)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val username = account?.displayName
                saveUsername(username ?: "Unknown") // 사용자 이름이 없을 경우
                saveLoginStatus(true)   // 로그인 상태 저장
                moveToMainScreen()  // MainActivity로 이동
            } catch (e: ApiException) {
                // Google Sign In 실패 처리
                Log.e("LoginActivity", "Google sign in failed: ${e.statusCode}")
            }
        }
    }
    private fun moveToMainScreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // 현재 Activity 종료
    }
    private fun saveLoginStatus(isLoggedIn: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.apply()
    }
    private fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // 뒤로가기 누르면 앱 종료
        finishAffinity()
    }
    private fun saveUsername(username: String) {
        val editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.apply()
    }

    companion object {
        const val RC_SIGN_IN = 123
    }
}