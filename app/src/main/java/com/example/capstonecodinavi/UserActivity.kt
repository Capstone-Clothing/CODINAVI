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
    private val logIn = LoginActivity()
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
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
