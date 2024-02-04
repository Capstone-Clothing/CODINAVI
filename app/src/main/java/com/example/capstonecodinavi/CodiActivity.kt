package com.example.capstonecodinavi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.capstonecodinavi.databinding.ActivityCodiBinding

class CodiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCodiBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCodiBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}