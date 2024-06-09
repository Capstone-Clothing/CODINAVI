package com.example.capstonecodinavi.Weather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.capstonecodinavi.R
import org.json.JSONObject

class WeatherAdapter(private val weatherInfoList: ArrayList<JSONObject>) :
    RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_weather, parent, false)
        return WeatherViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val item = weatherInfoList[position]
        holder.timeTv.text = item.getString("time").substring(0 until 2) + "시"
        holder.weatherIV.setImageResource(getWeatherIconId(item.getString("weather"), item.getString("precipitationType")))
        holder.tempTv.text = item.getString("temp") + "º"
    }

    override fun getItemCount(): Int {
        return weatherInfoList.size
    }

    inner class WeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val timeTv: TextView = itemView.findViewById(R.id.timeTv)
        val weatherIV: ImageView = itemView.findViewById(R.id.weatherIV)
        val tempTv: TextView = itemView.findViewById(R.id.tempTv)
    }

    private fun getWeatherIconId(weather: String, weather2: String): Int {
        return when {
            weather.contains("흐림") -> when {
                weather2.contains("비") -> R.drawable.rainy
                weather2.contains("눈") -> R.drawable.snowy
                weather2.contains("비 또는 눈") -> R.drawable.rainy
                weather2.contains("소나기") -> R.drawable.rainy
                else -> R.drawable.cloudy
            }
            weather.contains("구름많음") -> when {
                weather2.contains("비") -> R.drawable.rainy
                weather2.contains("눈") -> R.drawable.snowy
                weather2.contains("비 또는 눈") -> R.drawable.rainy
                weather2.contains("소나기") -> R.drawable.rainy
                else -> R.drawable.cloudy
            }
            weather.contains("맑음") -> when {
                weather2.contains("비") -> R.drawable.rainy
                weather2.contains("눈") -> R.drawable.snowy
                weather2.contains("비 또는 눈") -> R.drawable.rainy
                weather2.contains("소나기") -> R.drawable.rainy
                else -> R.drawable.sunny
            }
            else -> R.drawable.sunny
        }
    }
}
