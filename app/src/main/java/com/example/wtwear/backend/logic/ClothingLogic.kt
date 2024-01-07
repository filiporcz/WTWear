package com.example.wtwear.backend.logic

import com.example.wtwear.backend.supabase.Clothing
import com.example.wtwear.backend.supabase.Clothings
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.dsl.isNull
import org.ktorm.dsl.or
import org.ktorm.entity.EntitySequence
import org.ktorm.entity.filter
import org.ktorm.expression.ArgumentExpression
import org.ktorm.expression.FunctionExpression
import org.ktorm.schema.ColumnDeclaring
import org.ktorm.schema.IntSqlType
import org.ktorm.schema.VarcharSqlType
import org.ktorm.support.postgresql.ilike

fun matchClothesWeather(
    clothes: EntitySequence<Clothing, Clothings>,
    tempName: String?,
    precipName: String?,
    windName: String?
): EntitySequence<Clothing, Clothings> {
    return clothes
        .filter { it.temp_name.ilike("%${tempName!!}%") or (it.temp_name.isNull() or (it.temp_name eq "")) and
                (it.precip_name.ilike("%${precipName!!}%") or (it.precip_name.isNull() or (it.precip_name eq ""))) and
                (it.wind_name.ilike("%${windName}%") or (it.wind_name.isNull() or (it.wind_name eq ""))) }
}

fun matchClothesGender(
    clothes: EntitySequence<Clothing, Clothings>,
    gender: String?
): EntitySequence<Clothing, Clothings> {
    return if (gender.isNullOrEmpty()) {
        clothes
    } else {
        clothes
            .filter { it.gender eq gender or (it.gender.isNull() or (it.gender eq "")) }
    }
}

fun matchClothesType(
    clothes: EntitySequence<Clothing, Clothings>,
    type: String
): EntitySequence<Clothing, Clothings> {
    return clothes
        .filter { it.clothing_type eq type }
}
