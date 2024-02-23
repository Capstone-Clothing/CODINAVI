package com.example.capstonecodinavi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.capstonecodinavi.databinding.ActivityIntroduceCameraBtnBinding

class IntroduceCameraBtn : AppCompatActivity() {
    private lateinit var binding: ActivityIntroduceCameraBtnBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroduceCameraBtnBinding.inflate(layoutInflater)
        setContentView(binding.root)
        action()
    }

    private fun action() {
        binding.homeBtn.setOnClickListener {
            finish()
        }

        binding.profileBtn.setOnClickListener {
            val intent = Intent(this, UserActivity::class.java)
            startActivity(intent)
        }
    }
}