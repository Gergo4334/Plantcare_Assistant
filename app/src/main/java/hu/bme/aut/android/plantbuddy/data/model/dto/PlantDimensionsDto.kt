package hu.bme.aut.android.plantbuddy.data.model.dto

import com.squareup.moshi.Json

data class PlantDimensionsDto(
    val type: String?,
    @field:Json(name = "min_value") val minValue: Int,
    @field:Json(name = "max_value") val maxValue: Int,
    val unit: String
)