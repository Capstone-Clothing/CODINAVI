package com.example.capstonecodinavi.User

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.capstonecodinavi.Main.MainActivity
import com.example.capstonecodinavi.R
import com.example.capstonecodinavi.databinding.ActivityGenderBinding

class GenderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGenderBinding
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGenderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 초기화
        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE)

        val gender = sharedPreferences.getInt("gender", -1)

        Log.d("gender", gender.toString())

        if (gender!=-1) {
            val btn = binding.radioGroup.getChildAt(gender) as? RadioButton
            btn?.isChecked = true
        }

        setTitle(" ")
        action()
    }

    private fun action() {
        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.genderChangeBtn.setOnClickListener {
            val index: Int = binding.radioGroup.indexOfChild(findViewById(binding.radioGroup.checkedRadioButtonId))
            saveGender(index)
            Toast.makeText(this, "...", Toast.LENGTH_SHORT).show()
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

    private fun saveGender(gender: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("gender", gender)
        editor.apply()
    }
}
