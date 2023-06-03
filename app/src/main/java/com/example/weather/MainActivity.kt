package com.example.weather;

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.weather.R
import com.example.weather.databinding.ActivityMainBinding
import com.example.weather.model.ForecastResponse
import com.example.weather.model.WeatherData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var locationManager: LocationManager
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Проверка разрешения на доступ к местоположению
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Разрешение на доступ к местоположению уже предоставлено
            requestLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            MIN_TIME_BETWEEN_UPDATES,
            MIN_DISTANCE_CHANGE_FOR_UPDATES,
            locationListener
        )
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            // Получаем координаты местоположения
            val latitude = location.latitude
            val longitude = location.longitude

            // Выполняем запрос погоды
            val apiService = RetrofitClient.create()
            val call = apiService.getWeatherData(latitude=latitude, longitude=longitude, currentWeather = true)
            Log.d("dotes", latitude.toString())
            Log.d("dotes", longitude.toString())

            call.enqueue(object : Callback<ForecastResponse> {
                override fun onResponse(call: Call<ForecastResponse>, response: Response<ForecastResponse>) {
                    if (response.isSuccessful) {
                        val weatherData = response.body()
                        if (weatherData != null) {
                            // Обработка полученных данных о погоде

                            displayWeatherData(weatherData)
                        }
                    } else {
                        // Ошибка запроса
                        Toast.makeText(
                            this@MainActivity,
                            "Failed to get weather data",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ForecastResponse>, t: Throwable) {
                    // Ошибка при выполнении запроса
                    Toast.makeText(
                        this@MainActivity,
                        "Failed to get weather data: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }

        override fun onProviderDisabled(provider: String) {
            // Провайдер местоположения отключен
        }

        override fun onProviderEnabled(provider: String) {
            // Провайдер местоположения включен
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            // Состояние провайдера местоположения изменилось
        }
    }

    private fun displayWeatherData(weatherData: ForecastResponse) {
        // Отображение данных о погоде в пользовательском интерфейсе
        val temperature = weatherData.currentWeather.temperature // Получение температуры
        // Получение влажности
        val windSpeed = weatherData.currentWeather.windSpeed // Получение скорости ветра

        binding.textViewTemperature.text = "Temperature: $temperature °C" // Отображение температуры
         Log.d("wthr", temperature.toString())
        // Отображение влажности
        binding.textViewWindSpeed.text = "Wind Speed: $windSpeed km/h" // Отображение скорости ветра
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
        private const val MIN_TIME_BETWEEN_UPDATES: Long = 1000
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 10f
    }
}