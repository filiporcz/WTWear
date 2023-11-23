package com.example.wtwear

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.wtwear.ui.theme.WTWearTheme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.compose.foundation.layout.Column
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun WeatherInfo(weatherData: WeatherResponse?) {
    if (weatherData != null) {
        Column {
            Text("Temperature: ${weatherData.main.temp} °C")
            Text("Humidity: ${weatherData.main.humidity}%")
            // Add other Text components for additional weather information
        }
    } else {
        // Handle case when weatherData is null
        Text("Weather data is null")
    }
}

@Composable
fun WeatherScreen(weatherService: WeatherService, apiKey: String, location: String) {
    var weatherData by remember { mutableStateOf<WeatherResponse?>(null) }

    LaunchedEffect(location, apiKey) {
        try {
            withContext(Dispatchers.IO) {
                val response = weatherService.getWeather(location, apiKey)
                if (response.isSuccessful) {
                    weatherData = response.body()
                } else {
                    // Handle API error
                    Log.e("Weather API", "Error: ${response.code()}")
                }
            }
        } catch (e: Exception) {
            // Handle network error
            Log.e("Weather API", "Network error: ${e.message}")
        }
    }

    // A surface container using the 'background' color from the theme
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // Call WeatherInfo composable and pass the weatherData
        WeatherInfo(weatherData)
    }
}

class MainActivity : ComponentActivity() {
    private val retrofit = Retrofit.Builder()
        .baseUrl("api.openweathermap.org/data/2.5/weather?q=London,uk&APPID=32e60a591b19acc174f0c20a4610964f")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val weatherService = retrofit.create(WeatherService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apiKey = "32e60a591b19acc174f0c20a4610964f"
        val location = "Bristol,England"

        setContent {
            WTWearTheme {
                WeatherScreen(weatherService, apiKey, location)
            }
        }
    }

    private fun recommendClothing(weatherData: WeatherResponse?) {
        weatherData?.let {
            val temperature = it.main.temp
            val condition = it.weather.firstOrNull()?.main

            // Implement clothing recommendation logic here
            if (temperature < 15) {
                // Recommend a jacket or sweater for cold weather
            } else if (temperature > 25) {
                // Recommend shorts and a t-shirt for hot weather
            } else {
                // Default recommendation for moderate weather
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WTWearTheme {
        Greeting("Android")
    }
}

@Preview(showBackground = true)
@Composable
fun CallWeatherInfoPreview() {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/data/2.5/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val weatherService = retrofit.create(WeatherService::class.java)
    val apiKey = "32e60a591b19acc174f0c20a4610964f"
    val location = "Bristol,England"

    var weatherData by remember { mutableStateOf<WeatherResponse?>(null) }

    // Use LaunchedEffect to trigger the weather data fetch
    LaunchedEffect(location, apiKey) {
        try {
            withContext(Dispatchers.IO) {
                val response = weatherService.getWeather(location, apiKey)
                if (response.isSuccessful) {
                    weatherData = response.body()
                } else {
                    // Handle API error
                    Log.e("Weather API", "Error: ${response.code()}")
                }
            }
        } catch (e: Exception) {
            // Handle network error
            Log.e("Weather API", "Network error: ${e.message}")
        }
    }

    // A surface container using the 'background' color from the theme
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // Call WeatherInfo composable and pass the weatherData
        WeatherInfo(weatherData)
    }
}