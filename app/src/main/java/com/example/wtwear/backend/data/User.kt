package com.example.wtwear.backend.data

import com.example.wtwear.backend.logic.matchClothesGender
import com.example.wtwear.backend.logic.matchClothesType
import com.example.wtwear.backend.logic.matchClothesWeather
import com.example.wtwear.backend.logic.matchPrecip
import com.example.wtwear.backend.logic.matchTemp
import com.example.wtwear.backend.logic.matchWind
import com.example.wtwear.backend.supabase.Clothing
import com.example.wtwear.backend.supabase.clothings
import com.example.wtwear.backend.supabase.database
import org.ktorm.entity.toList

data class User(
    //val latitude: String,
    //val longitude: String,
    val city: String,
    val country: String,
    val gender: String?
) {
    private val weather = fetchWSData(city, country)
    private val findTemperature = matchTemp(weather.temperature)
    private val findPrecipitation = matchPrecip(weather.precipitation)
    private val findWind = matchWind(weather.wind)

    fun weatherInfo(): WSWeather {
        return weather
    }

    fun clothes(): Clothes {
        var clothes = database.clothings
        clothes = matchClothesWeather(clothes, findTemperature, findPrecipitation, findWind)
        clothes = matchClothesGender(clothes, gender)

        return Clothes(
            hat = matchClothesType(clothes, "hat").toList(),
            top = matchClothesType(clothes, "top").toList(),
            trousers = matchClothesType(clothes, "trousers").toList(),
            shoes = matchClothesType(clothes, "shoes").toList()
        )
    }
}

data class Clothes(
    val hat: List<Clothing>,
    val top: List<Clothing>,
    val trousers: List<Clothing>,
    val shoes: List<Clothing>
) {
    fun images(type: List<Clothing>): List<String?> {
        return type.map { it.image }
    }

    fun descriptions(type: List<Clothing>): List<String?> {
        return type.map { it.description }
    }
}