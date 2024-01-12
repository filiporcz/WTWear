package com.example.wtwear.backend.supabase

import org.ktorm.entity.Entity
import java.time.Instant

interface Clothing : Entity<Clothing> {
    companion object : Entity.Factory<Clothing>()
    val clothing_name: String
    val clothing_type: String
    val description: String?
    val image: String?
    val gender: String?
    val temp_name: String?
    val precip_name: String?
    val wind_name: String?
    val created_at: Instant
}

interface Temperature : Entity<Temperature> {
    companion object : Entity.Factory<Temperature>()
    val name: String
    val min: Int
    val max: Int
}

interface Precipitation : Entity<Precipitation> {
    companion object : Entity.Factory<Precipitation>()
    val name: String
    val min: Double
    val max: Double
}

interface Wind : Entity<Wind> {
    companion object : Entity.Factory<Wind>()
    val name: String
    val min: Int
    val max: Int
}
