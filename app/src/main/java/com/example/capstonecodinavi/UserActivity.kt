package com.example.capstonecodinavi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.capstonecodinavi.databinding.ActivityUserBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlin.math.log
import kotlin.math.sign

class UserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        action()
    }

    private fun action() {
        binding.homeBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.profileBtn.setOnClickListener {
            val intent = Intent(this, UserActivity::class.java)
            startActivity(intent)
            finish()
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
            signOut()
        }
    }
    private fun signOut() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        val auth = Firebase.auth

        googleSignInClient.signOut().addOnSuccessListener(this) {
            //logIn.updateUI(null)
            auth.signOut()
            finishAffinity()

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}

//질문 => MainActivity에서 UserActivity로 가서 로그아웃을 했을 때 LoginActivity로 넘어가는데 LoginActivity에서 뒤로가기를 누르면 MainActivity가 나옴.
//아마도 finish() 함수를 써야 할 것 같은데 어디서 써야될지를 모르겠음
//signout() 함수를 여기 말고 LoginActivity에 작성하고 UserActivity에서 LoginActivity 객체를 만들어서 사용할 수 없나? 해봤는데 안됨.