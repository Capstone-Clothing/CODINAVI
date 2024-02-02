package com.example.capstonecodinavi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.capstonecodinavi.databinding.ActivityGuideBinding

class GuideActivity : AppCompatActivity() {
    lateinit var binding: ActivityGuideBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}