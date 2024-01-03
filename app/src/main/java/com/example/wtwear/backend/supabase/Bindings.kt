package com.example.wtwear.backend.supabase

import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.*

val Database.clothings get() = this.sequenceOf(Clothings)
val Database.temperatures get() = this.sequenceOf(Temperatures)
val Database.precipiations get() = this.sequenceOf(Precipitations)
val Database.winds get() = this.sequenceOf(Winds)

object Clothings : Table<Clothing>("Clothing") {
    val clothing_name = varchar("clothing_name").primaryKey().bindTo { it.clothing_name }
    val clothing_type = varchar("clothing_type").bindTo { it.clothing_type }
    val description = text("description").bindTo { it.description }
    val image = varchar("image").bindTo { it.image }
    val gender = varchar("gender").bindTo { it.gender }
    //val temp_name = varchar("temp_name").bindTo { it.temp?.name }
    //val precip_name = varchar("precip_name").bindTo { it.precip?.name }
    //val wind_name = varchar("wind_name").bindTo { it.wind?.name }
    val temp_name = varchar("temp_name").bindTo { it.temp_name }
    val precip_name = varchar("precip_name").bindTo { it.precip_name }
    val wind_name = varchar("wind_name").bindTo { it.wind_name }
    val created_at = timestamp("created_at").bindTo { it.created_at }
}

object Temperatures : Table<Temperature>("Temperature") {
    val name = varchar("name").primaryKey().bindTo { it.name }
    val min = int("min").bindTo { it.min }
    val max = int("max").bindTo { it.max }
}

object Precipitations : Table<Precipitation>("Precipitation") {
    val name = varchar("name").primaryKey().bindTo { it.name }
    val min = double("min").bindTo { it.min }
    val max = double("max").bindTo { it.max }
}

object Winds : Table<Wind>("Wind") {
    val name = varchar("name").primaryKey().bindTo { it.name }
    val min = int("min").bindTo { it.min }
    val max = int("max").bindTo { it.max }
}
