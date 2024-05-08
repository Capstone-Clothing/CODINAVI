package com.example.capstonecodinavi.Guide

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.capstonecodinavi.Main.MainActivity
import com.example.capstonecodinavi.R
import com.example.capstonecodinavi.User.UserActivity
import com.example.capstonecodinavi.databinding.ActivityIntroduceAppcBtnBinding

class IntroduceAppcBtn : AppCompatActivity() {
    private lateinit var binding: ActivityIntroduceAppcBtnBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroduceAppcBtnBinding.inflate(layoutInflater)
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