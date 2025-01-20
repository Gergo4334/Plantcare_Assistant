package hu.bme.aut.android.plantbuddy.data.model.dto

import com.squareup.moshi.Json

data class ApiPlantDetailsDto(
    val type: String,
    val dimensions: PlantDimensionsDto,
    @field:Json(name = "watering_general_benchmark") val wateringGeneralBenchmark: WateringBenchmarkDto,
    val indoor: Boolean,
    @field:Json(name = "care_level") val careLevel: String,
    val flowers: Boolean,
    @field:Json(name = "flowering_season") val floweringSeason: String?,
    @field:Json(name = "growth_rate") val growthRate: String,
)