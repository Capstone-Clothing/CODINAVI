package com.example.capstonecodinavi.Guide

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.capstonecodinavi.Main.MainActivity
import com.example.capstonecodinavi.R
import com.example.capstonecodinavi.User.UserActivity
import com.example.capstonecodinavi.databinding.ActivityIntroduceCameraBtnBinding

class IntroduceCameraBtn : AppCompatActivity() {
    private lateinit var binding: ActivityIntroduceCameraBtnBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroduceCameraBtnBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(" ")
        action()
    }

    private fun action() {
        binding.menuBottomNav.setOnItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.menu_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menu_user -> {
                    val intent = Intent(this, UserActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
}