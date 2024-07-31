package com.example.capstonecodinavi.Recommend

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.capstonecodinavi.Camera.CameraActivity
import com.example.capstonecodinavi.Main.MainActivity
import com.example.capstonecodinavi.R
import com.example.capstonecodinavi.User.UserActivity
import com.example.capstonecodinavi.databinding.ActivitySearchOccasionBinding

class SearchOccasionActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchOccasionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchOccasionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(" ")
        action()

        binding.weddingBtn.setOnClickListener {
            navigateToOccasionActivity("wedding")
        }

        binding.funeralBtn.setOnClickListener {
            navigateToOccasionActivity("funeral")
        }

        binding.interviewBtn.setOnClickListener {
            navigateToOccasionActivity("interview")
        }

        binding.exerciseBtn.setOnClickListener {
            navigateToOccasionActivity("exercise")
        }
    }

    private fun navigateToOccasionActivity(occasion: String) {
        val intent = Intent(this, CameraActivity::class.java)
        intent.putExtra("occasion", occasion)
        startActivity(intent)
    }

    private fun action() {
        binding.backBtn.setOnClickListener {
            finish()
        }

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