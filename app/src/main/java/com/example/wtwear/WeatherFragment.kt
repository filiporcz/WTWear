package com.example.wtwear

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer

class WeatherFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_weather, container, false)

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

        userViewModel.weather.observe(viewLifecycleOwner, Observer {
            Log.d("userViewModel Test Weather:", it.toString())

            temperature.text = it.temperature.toString()
            feelsLike.text = it.feelsLike.toString()

            precipitation.text = it.precipitation.toString()
            uvIndex.text = it.uvIndex.toString()

            humidity.text = it.humidity.toString()
            pressure.text = it.pressure.toString()

            cloudCover.text = it.cloudCover.toString()
            windSpeed.text = it.wind.toString()

            windDegree.text = it.windDegree.toString()
            windDirection.text = it.windDir
        })

        return view
    }
}