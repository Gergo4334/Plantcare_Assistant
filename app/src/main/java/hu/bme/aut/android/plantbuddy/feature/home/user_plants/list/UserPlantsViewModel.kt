package hu.bme.aut.android.plantbuddy.feature.home.user_plants.list

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.bme.aut.android.plantbuddy.domain.interactor.firebase.storage.FirebaseStorageServiceInteractor
import hu.bme.aut.android.plantbuddy.domain.interactor.firebase.firestore.FirestoreRepositoryInteractor
import hu.bme.aut.android.plantbuddy.domain.model.plant.PlantDimensions
import hu.bme.aut.android.plantbuddy.domain.model.plant.WateringPeriod
import hu.bme.aut.android.plantbuddy.domain.model.plant.user.UserPlant
import hu.bme.aut.android.plantbuddy.domain.model.plant.state.UserPlantListState
import hu.bme.aut.android.plantbuddy.domain.model.plant.filter.IndoorOutdoorFilter
import hu.bme.aut.android.plantbuddy.domain.model.plant.filter.UserPlantFilterButtonState
import hu.bme.aut.android.plantbuddy.ui.model.toUiText
import hu.bme.aut.android.plantbuddy.util.UiEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class UserPlantsViewModel @Inject constructor(
    private val firestoreInteractor: FirestoreRepositoryInteractor,
    private val firebaseStorageInteractor: FirebaseStorageServiceInteractor
): ViewModel() {
    private val _plantListState = MutableStateFlow(UserPlantListState())
    val plantListState = _plantListState.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()
    var successMessage = mutableStateOf("")

    var newPlantName by mutableStateOf("")
    var newPlantCycle by mutableStateOf("")
    var newPlantWateringPeriodValue by mutableStateOf("")
    val newPlantWateringPeriodUnit = "days"
    var newPlantLastWateredDate by mutableStateOf(LocalDate.now())
    var newPlantIsFavourite by mutableStateOf(false)
    var newPlantIndoor by mutableStateOf(false)
    var newPlantImage by mutableStateOf<String?>(null)

    fun onEvent(event: UserPlantEvent) {
        when (event) {
            UserPlantEvent.FetchPlants -> {
                fetchPlantsFromFirebase()
                filterPlants()
            }
            is UserPlantEvent.FilterButtonChanged -> {
                val newState = event.state
                _plantListState.update { it.copy(filterButtonState = newState) }
                filterPlants()
            }
            UserPlantEvent.SaveNewPlant -> {
                saveNewPlant()
                filterPlants()
            }
            is UserPlantEvent.UpdateFavouriteStatus -> {
                updateFavouriteStatus(event.plant)
                filterPlants()
            }

            is UserPlantEvent.SearchTextChanged -> {
                val newText = event.searchText.trim()
                _plantListState.update { it.copy(searchText = newText) }
                filterPlants()
            }

            is UserPlantEvent.IndoorFilterChanged -> {
                _plantListState.update { it.copy(indoorFilterState = event.state) }
                filterPlants()
            }

            is UserPlantEvent.WaterPlant -> {
                waterPlant(event.plant)
            }
        }
    }
    private fun fetchPlantsFromFirebase() {
        viewModelScope.launch {
            firestoreInteractor.getPlants()
                .onStart {
                    _plantListState.update {
                        it.copy(isLoading = true)
                    }
                }
                .catch { exception ->
                    _uiEvent.send(UiEvent.Failure(exception.toUiText()))
                    _plantListState.update { it.copy(isLoading = false) }
                    Log.e("UserPlantsViewModel", "Error in coroutine: ${exception.message}")
                }
                .collect { plants ->
                    val sortedPlants = plants.sortedBy { plant ->
                        val wateringPeriodValue = plant.wateringPeriod.value?.toIntOrNull() ?: Int.MAX_VALUE
                        val wateringPeriodMultiplier = when (plant.wateringPeriod.unit.lowercase(Locale.ROOT)) {
                            "days" -> 1
                            "weeks" -> 7
                            "months" -> 30
                            else -> 0
                        }
                        val nextWateringDate = plant.lastWateredDate.plusDays(wateringPeriodValue.toLong() * wateringPeriodMultiplier.toLong())
                        val daysUntilNextWatering = ChronoUnit.DAYS.between(LocalDate.now(), nextWateringDate)

                        daysUntilNextWatering
                    }

                    _plantListState.update {
                        it.copy(plantList = sortedPlants, filteredList = sortedPlants, isLoading = false)
                    }
                }
        }
    }
    private fun updateFavouriteStatus(plant: UserPlant) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val updatedPlant = plant.copy(isFavourite = !plant.isFavourite)
                firestoreInteractor.updatePlant(updatedPlant)
                updatePlantLists(updatedPlant)
            } catch (e: Exception) {
                _uiEvent.send(UiEvent.Failure(e.toUiText()))
            }
        }
    }
    private fun saveNewPlant() {
        val newPlant = UserPlant(
            name = newPlantName,
            type = null,
            cycle = newPlantCycle,
            wateringPeriod = WateringPeriod(unit = newPlantWateringPeriodUnit, value = newPlantWateringPeriodValue),
            lastWateredDate = newPlantLastWateredDate,
            sunlight = null,
            image = newPlantImage,
            isFavourite = newPlantIsFavourite,
            indoor = newPlantIndoor,
            dimensions = PlantDimensions(type = null, minValue = 0, maxValue = 0, unit = "cm")
        )
        viewModelScope.launch(Dispatchers.IO) {
            try {
                firestoreInteractor.savePlant(newPlant)?.let { plantId ->
                    val plantWithId = newPlant.copy(id = plantId)
                    _plantListState.update { currentState ->
                        val updatedList = currentState.plantList.toMutableList().apply {
                            add(plantWithId)
                        }
                        currentState.copy(plantList = updatedList)
                    }
                    uploadPlantImageAndUpdate(plantWithId)
                }

                successMessage.value = "Plant saved"
                _uiEvent.send(UiEvent.Success)
            } catch (e: Exception) {
                _uiEvent.send(UiEvent.Failure(e.toUiText()))
            }
        }
    }
    private fun filterPlants() {
        val filteredList = _plantListState.value.plantList.filter { plant ->
            indoorFilter(plant) && favouriteFilter(plant) && textFilter(plant)
        }
        _plantListState.update { it.copy(filteredList = filteredList) }
    }
    private fun indoorFilter(plant: UserPlant): Boolean {
        return when (_plantListState.value.indoorFilterState) {
            IndoorOutdoorFilter.INDOOR -> plant.indoor
            IndoorOutdoorFilter.OUTDOOR -> !plant.indoor
        }
    }
    private fun favouriteFilter(plant: UserPlant): Boolean {
        return if (_plantListState.value.filterButtonState == UserPlantFilterButtonState.FAVOURITE) {
            plant.isFavourite
        } else {
            true
        }
    }
    private fun textFilter(plant: UserPlant): Boolean {
        if (_plantListState.value.searchText.isBlank()) {
            return true
        }

        return when (_plantListState.value.filterButtonState) {
            UserPlantFilterButtonState.NAME -> plant.name.contains(_plantListState.value.searchText, ignoreCase = true)
            UserPlantFilterButtonState.TYPE -> plant.type?.contains(_plantListState.value.searchText, ignoreCase = true) ?: false
            UserPlantFilterButtonState.FAVOURITE -> plant.isFavourite && plant.name.contains(_plantListState.value.searchText, ignoreCase = true)
        }
    }
    private suspend fun uploadPlantImageAndUpdate(plant: UserPlant) {
        try {
            if (!newPlantImage.isNullOrEmpty() || !newPlantImage.isNullOrBlank()) {
                val result = firebaseStorageInteractor.uploadImage(newPlantImage!!.toUri(), plant.id)
                val imageUrl = result.getOrThrow()
                val updatedPlant = plant.copy(image = imageUrl)
                firestoreInteractor.updatePlant(updatedPlant)
                updatePlantLists(updatedPlant)
            } else {
                Log.e("UploadPlantImage", "No image to upload")
            }
        } catch (e: Exception) {
            Log.e("UploadPlantImage", "Image upload failed: ${e.message}")
        }
    }
    private fun waterPlant(plant: UserPlant) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val updatedPlant = plant.copy(lastWateredDate = LocalDate.now())
                firestoreInteractor.updatePlant(updatedPlant)
                updatePlantLists(updatedPlant)
                successMessage.value = "Plant last watered date updated"
                _uiEvent.send(UiEvent.Success)
            } catch (e: Exception) {
                _uiEvent.send(UiEvent.Failure(e.toUiText()))
            }
        }
    }
    private fun updatePlantLists(updatedPlant: UserPlant) {
        _plantListState.update { currentState ->
            val updatedList = currentState.plantList.map { currentPlant ->
                if (currentPlant.id == updatedPlant.id) updatedPlant else currentPlant
            }
            val updatedFilteredList = currentState.filteredList.map { currentPlant ->
                if (currentPlant.id == updatedPlant.id) updatedPlant else currentPlant
            }
            currentState.copy(plantList = updatedList, filteredList = updatedFilteredList)
        }
    }
    fun updatePlantName(name: String) {
        newPlantName = name
    }
    fun updatePlantCycle(cycle: String) {
        newPlantCycle = cycle
    }
    fun updateWateringPeriodValue(value: String) {
        newPlantWateringPeriodValue = value
    }
    fun updateLastWateredDate(date: LocalDate) {
        newPlantLastWateredDate = date
    }
    fun updateIsFavourite(isFavourite: Boolean) {
        newPlantIsFavourite = isFavourite
    }
    fun updateIndoorStatus(isIndoor: Boolean) {
        newPlantIndoor = isIndoor
    }
    fun updateNewPlantImage(uri: Uri) {
        newPlantImage = uri.toString()
    }
}

sealed class UserPlantEvent {
    object FetchPlants: UserPlantEvent()
    data class FilterButtonChanged(val state: UserPlantFilterButtonState): UserPlantEvent()
    data class SearchTextChanged(val searchText: String): UserPlantEvent()
    data class IndoorFilterChanged(val state: IndoorOutdoorFilter): UserPlantEvent()
    object SaveNewPlant: UserPlantEvent()
    data class UpdateFavouriteStatus(val plant: UserPlant): UserPlantEvent()
    data class WaterPlant(val plant: UserPlant): UserPlantEvent()
}