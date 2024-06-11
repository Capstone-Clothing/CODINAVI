package com.example.capstonecodinavi.Weather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.capstonecodinavi.R
import com.example.capstonecodinavi.databinding.ItemWeatherBinding
import org.json.JSONObject

class WeatherAdapter(var weatherList: ArrayList<Weather>) : RecyclerView.Adapter<WeatherViewHolder>() {

    val itemClicked = MutableLiveData<Weather>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val binding = ItemWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WeatherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val item = weatherList[position]
        holder.bindData(item)
    }

    override fun getItemCount(): Int {
        return weatherList.size
    }
}