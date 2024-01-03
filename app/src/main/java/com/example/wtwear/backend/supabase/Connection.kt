package com.example.wtwear.backend.supabase

import org.ktorm.database.Database

val database = Database.connect(
    url = "jdbc:postgresql://db.lwalmarsdlzmzzuvseus.supabase.co:5432/",
    user = "postgres",
    password = "transpire-tragedy-skylight"
)
