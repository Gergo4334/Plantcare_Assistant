package hu.bme.aut.android.plantbuddy.feature.home.user_plants.details

import android.net.Uri
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.bme.aut.android.plantbuddy.domain.interactor.api.ai.AimlApiServiceInteractor
import hu.bme.aut.android.plantbuddy.domain.interactor.firebase.firestore.FirestoreRepositoryInteractor
import hu.bme.aut.android.plantbuddy.domain.interactor.firebase.storage.FirebaseStorageServiceInteractor
import hu.bme.aut.android.plantbuddy.domain.model.plant.PlantDimensions
import hu.bme.aut.android.plantbuddy.domain.model.plant.WateringPeriod
import hu.bme.aut.android.plantbuddy.domain.model.plant.state.UserPlantDetailsState
import hu.bme.aut.android.plantbuddy.domain.model.plant.user.UserPlant
import hu.bme.aut.android.plantbuddy.domain.model.plant.user.sampleUserPlant
import hu.bme.aut.android.plantbuddy.feature.home.user_plants.list.UserPlantEvent
import hu.bme.aut.android.plantbuddy.ui.model.toUiText
import hu.bme.aut.android.plantbuddy.util.UiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class UserPlantDetailsViewModel @Inject constructor(
    private val firestoreInteractor: FirestoreRepositoryInteractor,
    private val storageInteractor: FirebaseStorageServiceInteractor,
    private val aimlApiInteractor: AimlApiServiceInteractor,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    val plantId = savedStateHandle["plantId"] ?: ""
    val _plantState = MutableStateFlow(UserPlantDetailsState())
    val plantState = _plantState.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()
    var successMessage = mutableStateOf("")

    var editingName = mutableStateOf("")
    var editingWateringValue = mutableStateOf("")
    var editingWateringPeriod = mutableStateOf("")
    var editingLastWateredDate = mutableStateOf(LocalDate.now())
    var editingType = mutableStateOf("")
    var editingCycle = mutableStateOf("")
    var editingSunlight = mutableStateOf("")
    var editingIndoor = mutableStateOf(false)
    var editingDimensionMinValue = mutableIntStateOf(_plantState.value.plant?.dimensions?.minValue ?: 0)
    var editingDimensionMaxValue = mutableIntStateOf(_plantState.value.plant?.dimensions?.maxValue ?: 0)
    var editingDimensionUnit = mutableStateOf("")
    var editingImage = mutableStateOf(_plantState.value.plant?.image)
    fun onEvent(event: UserPlantDetailsEvent) {
        when (event) {
            UserPlantDetailsEvent.FetchPlantDetails -> {
                getData()
            }
            UserPlantDetailsEvent.SaveModifications -> {
                saveModifications()
            }
            UserPlantDetailsEvent.UpdateFavouriteStatus -> {
                updateFavouriteStatus()
            }
            UserPlantDetailsEvent.WaterPlant -> {
                waterPlant()
            }
            UserPlantDetailsEvent.EditDetails -> {
                toggleIsEditing()
            }
            UserPlantDetailsEvent.CancelEditing -> {
                cancelEditing()
            }
        }
    }

    private fun getData() {
        viewModelScope.launch {
            _plantState.update { it.copy(isLoading = true) }
            try {
                if (plantId.isNotEmpty()) {
                    val firestorePlant = firestoreInteractor.getPlantById(plantId)
                    firestorePlant?.let { plant ->
                        _plantState.update { it.copy(plant = plant, isLoading = false) }
                        updateEditingFields()
                        generateAiDescription()
                        _uiEvent.send(UiEvent.Success)
                    }
                }
            } catch (e: Exception) {
                _plantState.update { it.copy(isLoading = false) }
                _uiEvent.send(UiEvent.Failure(e.toUiText()))
            }
        }
    }
    private suspend fun generateAiDescription() {
        _plantState.value.plant?.let { userPlant ->
            val descriptionResponse = aimlApiInteractor.generateAiText(
                    model = "gpt-4",
                    systemPrompt = "You are a plant care expert. Provide detailed and accurate information about plants.",
                    userPrompt = "Tell me about ${userPlant.name} plant care.",
                    maxtoken = 150
            )
            _plantState.update { state ->
                state.copy(
                    aiDescription = descriptionResponse.fold(
                        onSuccess = { it },
                        onFailure = { "Failed to retrieve description" }
                    )
                )
            }
        }
    }
    private fun updateEditingFields() {
        editingName.value = _plantState.value.plant?.name ?: ""
        editingWateringValue.value = _plantState.value.plant?.wateringPeriod?.value ?: ""
        editingWateringPeriod.value = _plantState.value.plant?.wateringPeriod?.unit ?: ""
        editingLastWateredDate.value = _plantState.value.plant?.lastWateredDate ?: LocalDate.now()
        editingType.value = _plantState.value.plant?.type ?: ""
        editingCycle.value = _plantState.value.plant?.cycle ?: ""
        editingSunlight.value = _plantState.value.plant?.sunlight ?: ""
        editingIndoor.value = _plantState.value.plant?.indoor ?: false
        editingDimensionMinValue.intValue = _plantState.value.plant?.dimensions?.minValue ?: 0
        editingDimensionMaxValue.intValue = _plantState.value.plant?.dimensions?.maxValue ?: 0
        editingDimensionUnit.value = _plantState.value.plant?.dimensions?.unit ?: "days"
        editingImage.value = _plantState.value.plant?.image
    }
    private fun saveModifications() {
        viewModelScope.launch {
            _plantState.update { it.copy(isLoading = true) }
            try {
                _plantState.value.plant?.let { currentPlant ->
                    val imageUploadResult = storageInteractor.uploadImage(Uri.parse(editingImage.value), currentPlant.id)
                    val updatedImageUri = if (imageUploadResult.isSuccess) {
                        imageUploadResult.getOrNull()
                    } else {
                        currentPlant.image
                    }
                    val modifiedPlant = currentPlant.copy(
                        name = editingName.value,
                        type = editingType.value,
                        cycle = editingCycle.value,
                        wateringPeriod = WateringPeriod(
                            value = editingWateringValue.value,
                            unit = editingWateringPeriod.value
                        ),
                        lastWateredDate = editingLastWateredDate.value,
                        sunlight = editingSunlight.value,
                        image = updatedImageUri,
                        indoor = editingIndoor.value,
                        dimensions = PlantDimensions(
                            minValue = editingDimensionMinValue.intValue,
                            maxValue = editingDimensionMaxValue.intValue,
                            unit = editingDimensionUnit.value
                        )
                    )

                    _plantState.update { it.copy(plant = modifiedPlant, isEditing = false, isLoading = false) }
                    firestoreInteractor.updatePlant(modifiedPlant)
                    successMessage.value = "Modifications saved successfully"
                    _uiEvent.send(UiEvent.Success)
                }
            } catch (e: Exception) {
                _uiEvent.send(UiEvent.Failure(e.toUiText()))
            } finally {
                _plantState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun toggleIsEditing() {
        _plantState.update { it.copy(isEditing = !it.isEditing) }
    }

    private fun updateFavouriteStatus() {
        viewModelScope.launch {
            try {
                _plantState.value.plant?.let { plant ->
                    plant.isFavourite = !plant.isFavourite
                    firestoreInteractor.updatePlant(plant)
                }
            } catch (e: Exception) {
                _uiEvent.send(UiEvent.Failure(e.toUiText()))
            }
        }
    }

    private fun waterPlant() {
        viewModelScope.launch {
            try {
                _plantState.value.plant?.let { plant ->
                    plant.lastWateredDate = LocalDate.now()
                    firestoreInteractor.updatePlant(plant)
                }
                successMessage.value = "Plant last watered date updated"
                _uiEvent.send(UiEvent.Success)
            } catch (e: Exception) {
                _uiEvent.send(UiEvent.Failure(e.toUiText()))
            }
        }
    }

    private fun cancelEditing() {
        _plantState.update { it.copy(isEditing = false) }
        updateEditingFields()
    }

    fun updatePlantName(newName: String) {
        editingName.value = newName
    }

    fun updatePlantType(newType: String) {
        editingType.value = newType
    }

    fun updatePlantCycle(newCycle: String) {
        editingCycle.value = newCycle
    }

    fun updateWateringPeriodUnit(newUnit: String) {
        editingWateringPeriod.value = newUnit
    }

    fun updateWateringPeriodValue(newValue: String) {
        editingWateringValue.value = newValue
    }

    fun updateLastWateredDate(newDate: LocalDate) {
        editingLastWateredDate.value = newDate
    }

    fun updatePlantSunlight(newSunlight: String) {
        editingSunlight.value = newSunlight
    }

    fun updatePlantIndoorStatus(isIndoor: Boolean) {
        editingIndoor.value = isIndoor
    }
    fun updatePlantImage(newUri: Uri) {
        editingImage.value = newUri.toString()
    }
    fun updateDimensionMin(newValue: Int) {
        editingDimensionMinValue.intValue = newValue
    }
    fun increaseDimensionMin() {
        editingDimensionMinValue.intValue++
    }

    fun decreaseDimensionMin() {
        editingDimensionMinValue.intValue--
    }

    fun updateDimensionMax(newValue: Int) {
        editingDimensionMaxValue.intValue = newValue
    }
    fun increaseDimensionMax() {
        editingDimensionMaxValue.intValue++
    }
    fun decreaseDimensionMax() {
        editingDimensionMaxValue.intValue--
    }

    fun updateDimensionUnit(newValue: String) {
        editingDimensionUnit.value = newValue
    }
}

sealed class UserPlantDetailsEvent {
    object FetchPlantDetails: UserPlantDetailsEvent()
    object UpdateFavouriteStatus: UserPlantDetailsEvent()
    object WaterPlant: UserPlantDetailsEvent()
    object SaveModifications: UserPlantDetailsEvent()
    object EditDetails: UserPlantDetailsEvent()
    object CancelEditing: UserPlantDetailsEvent()
}