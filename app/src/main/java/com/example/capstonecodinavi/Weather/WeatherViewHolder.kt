package com.example.capstonecodinavi.Weather

import androidx.recyclerview.widget.RecyclerView
import com.example.capstonecodinavi.databinding.ItemWeatherBinding

class WeatherViewHolder(val binding: ItemWeatherBinding):RecyclerView.ViewHolder(binding.root) {
    fun bindData(weather: Weather) {
<<<<<<< HEAD
        binding.timeTv.text = weather.time.substring(0 until 2) + "시"
        binding.weatherIV.setImageResource(weather.weatherIconId)
        binding.tempTv.text = weather.temp + "º"
=======
        binding.timeTv.text = weather.time
        binding.weatherIV.setImageResource(weather.weatherIconId)
        binding.tempTv.text = weather.temp
>>>>>>> 2e56e66f9beb6484ba4ab1925494b9e2273632df
    }
}