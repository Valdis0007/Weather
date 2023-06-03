package com.example.weather.model

import com.google.gson.annotations.SerializedName

data class WeatherData(
    val temperature: Double,
    @SerializedName("windspeed")
    val windSpeed: Double
)