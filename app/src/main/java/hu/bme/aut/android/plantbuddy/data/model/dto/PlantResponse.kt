package hu.bme.aut.android.plantbuddy.data.model.dto

import com.squareup.moshi.Json

data class PlantResponse(
    val data: List<ApiPlantDto>,
    val to: Int,
    @field:Json(name = "per_page") val perPage: Int,
    @field:Json(name = "current_page") val currentPage: Int,
    val from: Int,
    @field:Json(name = "last_page") val lastPage: Int,
    val total: Int
)