package com.example.capstonecodinavi.User

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.capstonecodinavi.Guide.GuideActivity
import com.example.capstonecodinavi.Login.LoginActivity
import com.example.capstonecodinavi.databinding.FragmentUserBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class UserFragment : Fragment() {
    lateinit var binding: FragmentUserBinding
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentUserBinding.inflate(inflater)

        sharedPreferences = requireActivity().getSharedPreferences("loginPrefs", AppCompatActivity.MODE_PRIVATE)

        // 저장된 사용자 이름 가져오기
        val username = sharedPreferences.getString("username", "")

        // 사용자 이름이 비어있는 경우 "Unknown"을 사용
        val displayedName = if (username.isNullOrEmpty()) "Unknown" else username

        // TextView에 사용자 이름 설정
        binding.username.text = displayedName
        action()
        return binding.root
    }

    private fun action() {
        binding.genderBtn.setOnClickListener {
            val intent = Intent(requireActivity(), GenderActivity::class.java)
            requireActivity().startActivity(intent)
        }

        binding.guideBtn.setOnClickListener {
            val intent = Intent(requireActivity(), GuideActivity::class.java)
            requireActivity().startActivity(intent)
        }

        binding.administratorInquiryBtn.setOnClickListener {
            //관리자 문의 화면으로 이동
        }

        binding.logoutBtn.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        // Google 로그인 해제
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        googleSignInClient.signOut()

        // SharedPreferences에서 로그인 상태 변경
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", false)
        editor.apply()

        // 로그인 화면으로 이동
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        requireActivity().startActivity(intent)
        requireActivity().finish()
    }
}