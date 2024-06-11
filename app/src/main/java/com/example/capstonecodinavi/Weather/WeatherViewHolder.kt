package com.example.capstonecodinavi.Weather

import androidx.recyclerview.widget.RecyclerView
import com.example.capstonecodinavi.databinding.ItemWeatherBinding

class WeatherViewHolder(val binding: ItemWeatherBinding):RecyclerView.ViewHolder(binding.root) {
    fun bindData(weather: Weather) {
        binding.timeTv.text = weather.time
        binding.weatherIV.setImageResource(weather.weatherIconId)
        binding.tempTv.text = weather.temp
    }
}