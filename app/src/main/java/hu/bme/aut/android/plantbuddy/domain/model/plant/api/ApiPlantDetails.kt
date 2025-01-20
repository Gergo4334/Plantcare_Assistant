package hu.bme.aut.android.plantbuddy.domain.model.plant.api

import hu.bme.aut.android.plantbuddy.domain.model.plant.PlantDimensions
import hu.bme.aut.android.plantbuddy.domain.model.plant.WateringPeriod


data class ApiPlantDetails(
    val type: String,
    val dimensions: PlantDimensions,
    val wateringPeriod: WateringPeriod,
    val indoor: Boolean,
    val careLevel: String,
    val flowers: Boolean,
    val floweringSeason: String?,
    val growthRate: String,
)