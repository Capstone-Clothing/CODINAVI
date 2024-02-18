package com.example.codinavi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.codinavi.databinding.ActivityColorBinding

class ColorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityColorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityColorBinding.inflate(layoutInflater)
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
    }
}