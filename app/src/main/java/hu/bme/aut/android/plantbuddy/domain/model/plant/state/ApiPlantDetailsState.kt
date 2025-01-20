package hu.bme.aut.android.plantbuddy.domain.model.plant.state

import hu.bme.aut.android.plantbuddy.domain.model.google.GoogleMapsLocation
import hu.bme.aut.android.plantbuddy.domain.model.plant.api.ApiPlant

data class ApiPlantDetailsState(
    val plant: ApiPlant? = null,
    val aiDescription: String = "",
    val locations: List<GoogleMapsLocation> = emptyList(),
    val isLoading: Boolean = false
)