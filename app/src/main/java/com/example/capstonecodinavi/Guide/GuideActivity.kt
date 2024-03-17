package com.example.capstonecodinavi.Guide

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.capstonecodinavi.Main.MainActivity
import com.example.capstonecodinavi.User.UserActivity
import com.example.capstonecodinavi.databinding.ActivityGuideBinding

class GuideActivity : AppCompatActivity() {
    lateinit var binding: ActivityGuideBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(" ")
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

        binding.introduceCameraBtn.setOnClickListener {
            val intent = Intent(this, IntroduceCameraBtn::class.java)
            startActivity(intent)
        }

        binding.introduceWeatherBtn.setOnClickListener {
            val intent = Intent(this, IntroduceWeatherBtn::class.java)
            startActivity(intent)
        }
    }
}