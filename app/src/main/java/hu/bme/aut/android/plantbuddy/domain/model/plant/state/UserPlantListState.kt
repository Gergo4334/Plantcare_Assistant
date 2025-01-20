package hu.bme.aut.android.plantbuddy.domain.model.plant.state

import hu.bme.aut.android.plantbuddy.domain.model.plant.user.UserPlant
import hu.bme.aut.android.plantbuddy.domain.model.plant.filter.IndoorOutdoorFilter
import hu.bme.aut.android.plantbuddy.domain.model.plant.filter.UserPlantFilterButtonState

data class UserPlantListState(
    val plantList: List<UserPlant> = emptyList(),
    val filteredList: List<UserPlant> = emptyList(),
    val isLoading: Boolean = false,
    val searchText: String = "",
    val filterButtonState: UserPlantFilterButtonState = UserPlantFilterButtonState.NAME,
    val indoorFilterState: IndoorOutdoorFilter = IndoorOutdoorFilter.INDOOR
)