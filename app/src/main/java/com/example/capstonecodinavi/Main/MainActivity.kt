package com.example.capstonecodinavi.Main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.capstonecodinavi.User.UserFragment
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
    }
}
