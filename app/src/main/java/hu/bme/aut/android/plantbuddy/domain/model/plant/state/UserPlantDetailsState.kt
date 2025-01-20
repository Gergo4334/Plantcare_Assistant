package hu.bme.aut.android.plantbuddy.domain.model.plant.state

import hu.bme.aut.android.plantbuddy.domain.model.plant.user.UserPlant

data class UserPlantDetailsState(
    val plant: UserPlant? = null,
    val aiDescription: String = "",
    val isEditing: Boolean = false,
    val isLoading: Boolean = false
)