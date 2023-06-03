package com.example.weather.model

import com.google.gson.annotations.SerializedName

data class ForecastResponse (
    @SerializedName("current_weather")
    val currentWeather: WeatherData
)