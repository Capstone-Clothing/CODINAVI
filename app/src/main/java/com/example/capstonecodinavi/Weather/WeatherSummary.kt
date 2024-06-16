package com.example.capstonecodinavi.Weather

import java.io.Serializable

class WeatherSummary(
    var date: String,
    var time: String,
    var pty: String
) : Serializable {

}