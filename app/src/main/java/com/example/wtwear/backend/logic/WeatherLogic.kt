package com.example.wtwear.backend.logic

import com.example.wtwear.backend.supabase.database
import com.example.wtwear.backend.supabase.precipiations
import com.example.wtwear.backend.supabase.temperatures
import com.example.wtwear.backend.supabase.winds
import org.ktorm.dsl.*
import org.ktorm.entity.find

fun matchTemp(temp: Int): String? {
    return database.temperatures
        .find { temp greaterEq it.min or (it.min.isNull()) and
                (temp lessEq it.max or (it.max.isNull())) }
        ?.name
}

fun matchPrecip(precip: Double): String? {
    return database.precipiations
        .find { precip greaterEq it.min or (it.min.isNull()) and
                (precip lessEq it.max or (it.max.isNull())) }
        ?.name
}

fun matchWind(wind: Int): String? {
    return database.winds
        .find { wind greaterEq it.min or (it.min.isNull()) and
                (wind lessEq it.max or (it.max.isNull())) }
        ?.name
}