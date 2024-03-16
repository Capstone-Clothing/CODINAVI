package com.example.capstonecodinavi.Weather

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.capstonecodinavi.Main.MainActivity
import com.example.capstonecodinavi.User.UserActivity
import com.example.capstonecodinavi.databinding.ActivitySearchOtherlocationBinding

class SearchOtherlocation : AppCompatActivity() {
    lateinit var binding: ActivitySearchOtherlocationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchOtherlocationBinding.inflate(layoutInflater)
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

        binding.searchBtn.setOnClickListener {
            val intent = Intent(this, OtherlocationActivity::class.java)
            intent.putExtra("address",binding.searchEt.text.toString())
            startActivity(intent)
        }
    }
}