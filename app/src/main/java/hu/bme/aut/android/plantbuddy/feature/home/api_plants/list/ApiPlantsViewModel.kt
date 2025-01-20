package hu.bme.aut.android.plantbuddy.feature.home.api_plants.list

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.bme.aut.android.plantbuddy.domain.interactor.api.ai.AimlApiServiceInteractor
import hu.bme.aut.android.plantbuddy.domain.interactor.api.kindwise.KindwisePlantIdServiceInteractor
import hu.bme.aut.android.plantbuddy.domain.interactor.firebase.storage.FirebaseStorageServiceInteractor
import hu.bme.aut.android.plantbuddy.domain.interactor.plant.PlantRepositoryInteractor
import hu.bme.aut.android.plantbuddy.domain.model.plant.filter.ApiPlantFilterButtonState
import hu.bme.aut.android.plantbuddy.domain.model.plant.state.ApiPlantListState
import hu.bme.aut.android.plantbuddy.ui.model.toUiText
import hu.bme.aut.android.plantbuddy.util.UiEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ApiPlantsScreenViewModel @Inject constructor(
    private val repositoryInteractor: PlantRepositoryInteractor,
    private val storageInteractor: FirebaseStorageServiceInteractor,
    private val kindwisePlantIdApi: KindwisePlantIdServiceInteractor,
    private val aimlApi: AimlApiServiceInteractor
): ViewModel() {
    private val _plantListState = MutableStateFlow(ApiPlantListState())
    val plantListState = _plantListState.asStateFlow()
    private val currentPage get() = plantListState.value.currentPage

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()
    var successMessage = mutableStateOf("")

    fun onEvent(event: ApiPlantEvent) {
        when(event) {
            ApiPlantEvent.FetchNextPage -> {
                fetchNextPageFromApi()
                filterPlants()
            }
            ApiPlantEvent.FetchPlants -> {
                fetchPlantsFromApi()
            }
            is ApiPlantEvent.SearchTextChanged -> {
                val newText = event.searchText.trim()
                _plantListState.update { it.copy(searchText = newText) }
                filterPlants()
            }
            is ApiPlantEvent.FilterButtonChanged -> {
                val newState = event.state
                _plantListState.update { it.copy(filterButtonState = newState) }
            }

            is ApiPlantEvent.imageUriChanged -> {
                val newUri = event.uri
                _plantListState.update { it.copy(uriForImageRecognition = newUri.toString()) }
            }

            ApiPlantEvent.StartImageRecognition -> {
                uploadImageToStorageAndStartRecognition()
                //generateAiDescription()
            }
        }
    }

    private fun fetchPlantsFromApi() {
        viewModelScope.launch(Dispatchers.IO) {
            _plantListState.update { it.copy(isLoading = true) }
            try {
                val plants = repositoryInteractor.getAllPlants(currentPage, 20)
                _plantListState.update { it.copy(plantList = plants, filteredList = plants, isLoading = false) }
                successMessage.value = "Plants loaded successfully"
                _uiEvent.send(UiEvent.Success)
            } catch (e: Exception) {
                _plantListState.update { it.copy(isLoading = false) }
                _uiEvent.send(UiEvent.Failure(e.toUiText()))
            }
        }
    }

    private fun fetchNextPageFromApi() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _plantListState.update { it.copy(currentPage = it.currentPage+1) }
                val plants = repositoryInteractor.getAllPlants(currentPage, 20)
                _plantListState.update { it.copy(plantList = it.plantList+plants, isLoading = false) }
            } catch (e: Exception) {
                _uiEvent.send(UiEvent.Failure(e.toUiText()))
            }
        }
    }

    private fun filterPlants() {
        val filteredList = if (_plantListState.value.searchText.isBlank()) {
            _plantListState.value.plantList
        } else {
            _plantListState.value.plantList
                .filter { plant ->
                    when (_plantListState.value.filterButtonState) {
                        ApiPlantFilterButtonState.NAME -> {
                            plant.name.contains(_plantListState.value.searchText, ignoreCase = true)
                        }

                        ApiPlantFilterButtonState.WATERING -> {
                            plant.watering.contains(
                                _plantListState.value.searchText,
                                ignoreCase = true
                            )
                        }

                        ApiPlantFilterButtonState.CYCLE -> {
                            plant.cycle.contains(
                                _plantListState.value.searchText,
                                ignoreCase = true
                            )
                        }

                        ApiPlantFilterButtonState.SUNLIGHT -> {
                            plant.sunlight.last().contains(
                                _plantListState.value.searchText,
                                ignoreCase = true
                            )
                        }
                    }
                }
        }
        _plantListState.update { it.copy(filteredList = filteredList) }
    }

    private fun uploadImageToStorageAndStartRecognition() {
        viewModelScope.launch {
            try {
                _plantListState.value.uriForImageRecognition?.let { uriString ->
                    val result = storageInteractor.uploadImage(Uri.parse(uriString), forRecognition = true)
                    result.getOrNull()?.let { uploadedUri ->
                        _plantListState.update { it.copy(uriForImageRecognition = uploadedUri) }

                        getImageRecognition()
                    }
                }
            } catch (e: Exception) {
                _uiEvent.send(UiEvent.Failure(e.toUiText()))
            }
        }
    }

    private suspend fun getImageRecognition() {
        if (_plantListState.value.uriForImageRecognition == null) return
        try {
            val images = listOf(_plantListState.value.uriForImageRecognition!!)
            val response = kindwisePlantIdApi.identifyPlant(images, LocalDate.now(), true)

            _plantListState.update {
                it.copy(
                    recognitionResponseName = response.getOrThrow().result.classification.suggestions.first().name,
                    recognitionResponseImage = response.getOrThrow().result.classification.suggestions.first().similarImages?.first()?.url,
                    recognitionResponseProbability = response.getOrThrow().result.classification.suggestions.first().probability,
                    showResultDialog = true,
                )
            }
            generateAiDescription()
            Log.d("ApiPlantsViewModel", "Kindwise api response {response.getOrNull().toString()}")
        } catch (e: Exception) {
            _uiEvent.send(UiEvent.Failure(e.toUiText()))
            Log.e("ApiPlantsViewModel", "Error during kindwise api call", e)
        }
    }

    private suspend fun generateAiDescription() {
        try {
            val plantName = _plantListState.value.recognitionResponseName
            if (plantName.isNotEmpty()) {
                val descriptionResponse = aimlApi.generateAiText(
                    model = "gpt-4",
                    systemPrompt = "You are a plant care expert. Provide detailed and accurate information about plants.",
                    userPrompt = "Tell me some basic information about $plantName plant.",
                    maxtoken = 30
                )
                _plantListState.update { state ->
                    state.copy(
                        aiDescription = descriptionResponse.fold(
                            onSuccess = { it },
                            onFailure = { "Failed to retrieve description" }
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("ApiPlantsViewModel", "Ai description generation failed", e)
        }
    }

    fun closeShowResultDialog() {
        _plantListState.update { it.copy(showResultDialog = false) }
    }
}

sealed class ApiPlantEvent {
    object FetchPlants: ApiPlantEvent()
    object FetchNextPage: ApiPlantEvent()
    object StartImageRecognition: ApiPlantEvent()
    data class SearchTextChanged(val searchText: String): ApiPlantEvent()
    data class FilterButtonChanged(val state: ApiPlantFilterButtonState): ApiPlantEvent()
    data class imageUriChanged(val uri: Uri): ApiPlantEvent()
}