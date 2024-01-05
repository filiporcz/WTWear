package com.example.wtwear

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
                temperature.text = it.temperature.toString()
                feelsLike.text = it.feelsLike.toString()

                precipitation.text = it.precipitation.toString()
                windSpeed.text = it.wind.toString()
            } else if (unitPref == "imperial") {
                temperature.text = celsiusToFahrenheit(it.temperature).toString()
                feelsLike.text = celsiusToFahrenheit(it.feelsLike).toString()

                precipitation.text = mmToInches(it.precipitation).toString()
                windSpeed.text = cmToInches(it.wind).toString()
            }
            uvIndex.text = it.uvIndex.toString()

            humidity.text = it.humidity.toString()
            pressure.text = it.pressure.toString()

            cloudCover.text = it.cloudCover.toString()

            windDegree.text = it.windDegree.toString()
            windDirection.text = it.windDir
        }

        return view
    }
}