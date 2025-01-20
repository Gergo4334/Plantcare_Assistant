package hu.bme.aut.android.plantbuddy.data.model.dto

import com.squareup.moshi.Json

data class ApiPlantDto(
    val id: Int,
    @field:Json(name = "common_name") val commonName: String,
    val cycle: String,
    val watering: String,
    val sunlight: List<String>,
    @field:Json(name = "default_image") val defaultImage: PlantImageDto?
)
