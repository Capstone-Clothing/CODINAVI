package com.example.capstonecodinavi

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.capstonecodinavi.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    lateinit var binding: FragmentMainBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMainBinding.inflate(inflater)

        binding.guideBtn.setOnClickListener {
            val intent = Intent(requireActivity(), GuideActivity::class.java)
            requireActivity().startActivity(intent)
        }

        return binding.root
    }
}