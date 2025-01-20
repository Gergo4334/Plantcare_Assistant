package hu.bme.aut.android.plantbuddy.data.model.dto

import com.squareup.moshi.Json

data class PlantWithDetailsDto(
    // ApiPlantDto data
    val id: Int,
    @field:Json(name = "common_name") val commonName: String,
    val cycle: String,
    val watering: String,
    val sunlight: List<String>,
    @field:Json(name = "default_image") val defaultImage: PlantImageDto?,
    // ApiPlantDetailsDto data
    val type: String,
    val dimensions: PlantDimensionsDto,
    @field:Json(name = "watering_general_benchmark") val wateringGeneralBenchmark: WateringBenchmarkDto,
    val indoor: Boolean,
    @field:Json(name = "care_level") val careLevel: String,
    val flowers: Boolean,
    @field:Json(name = "flowering_season") val floweringSeason: String?,
    @field:Json(name = "growth_rate") val growthRate: String
)