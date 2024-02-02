package com.example.capstonecodinavi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.capstonecodinavi.databinding.ActivityWeatherBinding

class WeatherActivity : AppCompatActivity() {
    lateinit var binding: ActivityWeatherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}