package hu.bme.aut.android.plantbuddy.data.model.dto

import com.squareup.moshi.Json

data class PlantImageDto(
    @field:Json(name = "regular_url") val regularUrl: String,
    @field:Json(name = "medium_url") val mediumUrl: String,
    @field:Json(name = "small_url") val smallUrl: String,
    val thumbnail: String
)