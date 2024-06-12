package com.example.capstonecodinavi.Weather

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.capstonecodinavi.Main.MainActivity
import com.example.capstonecodinavi.R
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

        //  권한 설정
        requestPermission()
    }

    // 권한 설정 메소드
    private fun requestPermission() {
        // 버전 체크, 권한 허용했는지 체크
        if (Build.VERSION.SDK_INT >= 23 &&
            ContextCompat.checkSelfPermission(this@SearchOtherlocation, android.Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this@SearchOtherlocation,
                arrayOf(android.Manifest.permission.RECORD_AUDIO), 0)
        }
    }
    // test

    private fun startSpeechRecognition() {
        // 음성인식 인텐트 생성
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)    // 여분의 키
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")               // 언어 설정

        // 음성인식 실행
        try {
            startActivityForResult(intent, 123)
        } catch (e: Exception) {
            Toast.makeText(this@SearchOtherlocation, "음성인식을 지원하지 않습니다.", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123 && resultCode == RESULT_OK && data != null) {
            val matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val result = matches?.get(0)
            // 여기서 인식된 음성 결과(result) 처리
            binding.searchEt.setText(result)
        }
    }

    private fun action() {
        binding.backBtn.setOnClickListener {
            finish()
        }

        // speechBtn 클릭 시 음성인식 시작
        binding.speechBtn.setOnClickListener {
            startSpeechRecognition()
        }

        binding.searchBtn.setOnClickListener {
            val intent = Intent(this, OtherlocationActivity::class.java)
            intent.putExtra("address",binding.searchEt.text.toString())
            startActivity(intent)
        }

        binding.menuBottomNav.setOnItemSelectedListener { menuItem->
            when(menuItem.itemId) {
                R.id.menu_home -> {
                    // 홈 버튼 클릭 시 MainActivity로 이동
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