package com.example.codinavi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.codinavi.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.Auth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    lateinit var auth: FirebaseAuth
    lateinit var googleSignInClient: GoogleSignInClient

//    var googleLoginResult =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//
//        var data = result.data
//        var task = GoogleSignIn.getSignedInAccountFromIntent(data)
//        val account = task.getResult(ApiException::class.java)
//        firebaseAuthWithGoogle(account.idToken)
//    }
//
//    fun firebaseAuthWithGoogle(idToken: String?) {
//        var credential = GoogleAuthProvider.getCredential(idToken, null)
//        auth.signInWithCredential((credential))
//            .addOnCompleteListener { task ->
//                if(task.isSuccessful) {
//                    moveMainPage(task.result?.user)
//                }
//            }
//    }
//
//    override fun onStart() {
//        super.onStart()
//        val user = auth.currentUser
//
//        if (user != null) {
//            user.getIdToken(true).addOnCompleteListener { task ->
//                if(task.isSuccessful) {
//                    val idToken = task.result.token
//                    val homeMoveIntent = Intent(applicationContext, MainActivity::class.java)
//                    startActivity(homeMoveIntent)
//                }
//            }
//        }
//    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        auth = FirebaseAuth.getInstance()
//
//        binding.loginBtn.setOnClickListener {
//            signIn()
//        }
//
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
//            .requestEmail()
//            .requestProfile()
//            .build()
//
//        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

//    private fun signIn() {
//        val i = googleSignInClient.signInIntent
//        googleLoginResult.launch(i)
//    }
//
//    fun moveMainPage(user: FirebaseUser?) {
//        if (user != null) {
//            startActivity(Intent(this, MainActivity::class.java))
//        }
//    }

}