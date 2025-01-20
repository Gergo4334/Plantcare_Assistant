package hu.bme.aut.android.plantbuddy.domain.model.plant.api

import hu.bme.aut.android.plantbuddy.domain.model.plant.PlantImage

data class ApiPlant(
    val id: Int,
    val name: String,
    val cycle: String,
    val watering: String,
    val sunlight: List<String>,
    val image: PlantImage?,
    var details: ApiPlantDetails?
)