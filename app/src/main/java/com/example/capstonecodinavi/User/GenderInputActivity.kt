package com.example.capstonecodinavi.User

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.capstonecodinavi.R
import com.example.capstonecodinavi.databinding.ActivityGenderInputBinding

class GenderInputActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGenderInputBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGenderInputBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}