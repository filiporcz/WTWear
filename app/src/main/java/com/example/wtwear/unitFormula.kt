package com.example.wtwear

import kotlin.math.roundToInt

fun celsiusToFahrenheit(celsius: Int): Double {
    return (celsius * 1.8) + 32
}

fun kmhToMph(kmh: Int): Int {
    return (kmh / 1.609344).roundToInt()
}

fun mmToInches(mm: Double): Double {
    return mm / 2.54
}

fun cmToInches(cm: Int): Double {
    return cm / 2.54
}