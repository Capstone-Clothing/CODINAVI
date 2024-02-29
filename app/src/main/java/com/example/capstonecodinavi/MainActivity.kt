package com.example.capstonecodinavi

import android.Manifest
import android.Manifest.permission.CAMERA
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.capstonecodinavi.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var mainFragment: MainFragment
    private lateinit var userFragment: UserFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(" ")
        initUI()
        action()
    }

    private fun initUI() {
        mainFragment = MainFragment()
        userFragment = UserFragment()

        supportFragmentManager.beginTransaction().add(binding.mainFl.id, mainFragment).commit()
    }

    private fun action() {
        binding.homeBtn.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(binding.mainFl.id, mainFragment).commit()
        }

        binding.profileBtn.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(binding.mainFl.id, userFragment).commit()
        }

//        binding.cameraBtn.setOnClickListener {
//            val intent = Intent(this, CameraActivity::class.java)
//            startActivity(intent)
//        }
//        binding.guideBtn.setOnClickListener {
//            val intent = Intent(this, GuideActivity::class.java)
//            startActivity(intent)
//        }
//
//        binding.weatherBtn.setOnClickListener {
//            val intent = Intent(this, WeatherActivity::class.java)
//            startActivity(intent)
//        }
    }
}
