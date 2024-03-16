package com.example.capstonecodinavi.Main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.capstonecodinavi.Camera.CameraActivity
import com.example.capstonecodinavi.Guide.GuideActivity
import com.example.capstonecodinavi.Weather.WeatherActivity
import com.example.capstonecodinavi.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    lateinit var binding: FragmentMainBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMainBinding.inflate(inflater)

        binding.guideBtn.setOnClickListener {
            val intent = Intent(requireActivity(), GuideActivity::class.java)
            requireActivity().startActivity(intent)
        }
        binding.cameraBtn.setOnClickListener {
            val intent = Intent(requireActivity(), CameraActivity::class.java)
            requireActivity().startActivity(intent)
        }
        binding.guideBtn.setOnClickListener {
            val intent = Intent(requireActivity(), GuideActivity::class.java)
            requireActivity().startActivity(intent)
        }

        binding.weatherBtn.setOnClickListener {
            val intent = Intent(requireActivity(), WeatherActivity::class.java)
            requireActivity().startActivity(intent)
        }

        return binding.root
    }
}