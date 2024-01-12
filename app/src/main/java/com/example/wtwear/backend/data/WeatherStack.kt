package com.example.wtwear.backend.data

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.treeToValue
import org.http4k.client.ApacheClient
import org.http4k.core.BodyMode
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response

data class WSWeather(
    val temperature: Int,
    val precipitation: Double,
    val wind: Int,

    val windDegree: Int,
    val windDir: String,
    val pressure: Int,
    val humidity: Int,
    val cloudCover: Int,
    val feelsLike: Int,
    val uvIndex: Int,
)

val client = ApacheClient(responseBodyMode = BodyMode.Stream)
val mapper = jacksonObjectMapper()

fun fetchWSData(city: String, country: String): WSWeather {
    val fetch = fetchWeatherStack(city, country)
    return wsJSONtoData(fetch)
}

fun fetchWeatherStack(city: String, country: String): Response {
    val baseUrl = "http://api.weatherstack.com/current"
    val apiKey = "ebcadd5783986e5b400c3de58e18812b"
    val url = "${baseUrl}?access_key=${apiKey}&query=${city},${country}"

    val request = Request(Method.GET, url)
    return client(request)
}

fun wsJSONtoData(response: Response): WSWeather {
    val node = mapper.readTree(response.bodyString())
    val weatherInfo = node.get("current") // the weather info is stored in the current object

    return WSWeather(
        temperature = mapper.treeToValue(weatherInfo.get("temperature")),
        precipitation = mapper.treeToValue(weatherInfo.get("precip")),
        wind = mapper.treeToValue(weatherInfo.get("wind_speed")),

        windDegree = mapper.treeToValue(weatherInfo.get("wind_degree")),
        windDir = mapper.treeToValue(weatherInfo.get("wind_dir")),
        pressure = mapper.treeToValue(weatherInfo.get("pressure")),
        humidity = mapper.treeToValue(weatherInfo.get("humidity")),
        cloudCover = mapper.treeToValue(weatherInfo.get("cloudcover")),
        feelsLike = mapper.treeToValue(weatherInfo.get("feelslike")),
        uvIndex = mapper.treeToValue(weatherInfo.get("uv_index"))
    )
}
