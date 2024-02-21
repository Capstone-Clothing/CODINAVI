package com.example.capstonecodinavi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.capstonecodinavi.databinding.ActivityCameraBinding

class CameraActivity : AppCompatActivity() {
    lateinit var binding: ActivityCameraBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setClothImage()
        action()
    }

    private fun setClothImage() {
        binding.clothIv.setImageBitmap(MainActivity.imageBitmap)
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
        // 추가해야 할 것 : 밑의 두 개의 콜백 함수가 실행되기 위해서는 먼저 clothIv에 이미지가 있어야 함. -> if문으로 조건 걸어야 함.
        binding.codiBtn.setOnClickListener {
            val intent = Intent(this, CodiActivity::class.java)
            startActivity(intent)
        }

        binding.colorBtn.setOnClickListener {
            val intent = Intent(this, ColorActivity::class.java)
            startActivity(intent)
        }
    }
}