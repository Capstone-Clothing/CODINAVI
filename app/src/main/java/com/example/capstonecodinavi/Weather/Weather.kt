package com.example.capstonecodinavi.Weather

import java.io.Serializable

class Weather(
    var time: String,
    var weatherIconId: Int,
    var temp: String,
    var humidity: String,
    var weatherText: String,
    var precipitationProbability: String,
    var precipitation: String) : Serializable {
}