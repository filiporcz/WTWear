package com.example.wtwear

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class WeatherFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_weather, container, false)

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val unitPref = sharedPref?.getString("unit", "metric")
        Log.d("Current Unit Test:", unitPref.toString())

        val temperature = view.findViewById<TextView>(R.id.temperatureText)
        val feelsLike = view.findViewById<TextView>(R.id.feelsLikeText)

        val precipitation = view.findViewById<TextView>(R.id.precipitationImage)
        val uvIndex = view.findViewById<TextView>(R.id.uvIndexImage)

        val humidity = view.findViewById<TextView>(R.id.humidityImage)
        val pressure = view.findViewById<TextView>(R.id.pressureImage)

        val cloudCover = view.findViewById<TextView>(R.id.cloudCoverImage)
        val windSpeed = view.findViewById<TextView>(R.id.windSpeedImage)

        val windDegree = view.findViewById<TextView>(R.id.windDegreeImage)
        val windDirection = view.findViewById<TextView>(R.id.windDirectionImage)

        userViewModel.weather.observe(viewLifecycleOwner) {
            Log.d("userViewModel Test Weather:", it.toString())

            if (unitPref == "metric") {
                val temperatureText = "${it.temperature}°C"
                temperature.text = temperatureText
                val feelsLikeText = "Feels like: ${it.feelsLike}°C"
                feelsLike.text = feelsLikeText

                val precipitationText = "${it.precipitation} mm"
                precipitation.text = precipitationText
                val windSpeedText = "${it.wind} km/h"
                windSpeed.text = windSpeedText
            } else if (unitPref == "imperial") {
                val temperatureText = "${celsiusToFahrenheit(it.temperature)}°F"
                temperature.text = temperatureText
                val feelsLikeText = "Feels like: ${celsiusToFahrenheit(it.feelsLike)}°F"
                feelsLike.text = feelsLikeText

                val precipitationText = "${mmToInches(it.precipitation)}\""
                precipitation.text = precipitationText
                val windSpeedText = "${kmhToMph(it.wind)} mph"
                windSpeed.text = windSpeedText
            }

            val uvIndexText = "${it.uvIndex}/11"
            uvIndex.text = uvIndexText

            val humidityText = "${it.humidity}%"
            humidity.text = humidityText
            val pressureText = "${it.pressure} hPa"
            pressure.text = pressureText

            val cloudCoverText = "${it.cloudCover}%"
            cloudCover.text = cloudCoverText

            val windDegreeText = "${it.windDegree}°"
            windDegree.text = windDegreeText
            windDirection.text = it.windDir
        }

        return view
    }
}