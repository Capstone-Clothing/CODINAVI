package com.example.capstonecodinavi

import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.capstonecodinavi.databinding.ActivitySearchOtherlocationBinding
import java.util.Locale

class SearchOtherlocation : AppCompatActivity() {
    lateinit var binding: ActivitySearchOtherlocationBinding
    var lat : Double? = null
    var lng : Double? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchOtherlocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchBtn.setOnClickListener {
            val searchText = binding.searchEt.text.toString()
            getLatLng(searchText)
        }
    }

    fun getLatLng(address: String) {
        val getWeather = WeatherActivity()
        var weather: String? = null
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            geocoder.getFromLocationName(address, 1)?.let {
                Location("").apply {
                    Log.d("check1000","$it")
                    lat = it[0].latitude
                    lng = it[0].longitude
                    weather = getWeather.getCurrentWeather(lat!!, lng!!)

                    binding.currentWeatherTv.text = "날씨는 $weather"
                }
            }
        } catch (e : Exception){
            e.printStackTrace()
            getLatLng(address)
        }
    }
}