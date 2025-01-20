package hu.bme.aut.android.plantbuddy.feature.home.api_plants.details

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.bme.aut.android.plantbuddy.domain.interactor.api.ai.AimlApiServiceInteractor
import hu.bme.aut.android.plantbuddy.domain.interactor.plant.PlantRepositoryInteractor
import hu.bme.aut.android.plantbuddy.domain.model.google.GoogleMapsLocation
import hu.bme.aut.android.plantbuddy.domain.model.plant.state.ApiPlantDetailsState
import hu.bme.aut.android.plantbuddy.ui.model.toUiText
import hu.bme.aut.android.plantbuddy.util.UiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApiPlantsDetailsViewModel @Inject constructor(
    private val repositoryInteractor: PlantRepositoryInteractor,
    private val aiServiceInteractor: AimlApiServiceInteractor,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    val plantId = savedStateHandle["plantId"] ?: ""
    private val _plantState = MutableStateFlow(ApiPlantDetailsState())
    val plantState = _plantState.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: ApiPlantDetailsEvent) {
        when (event) {
            ApiPlantDetailsEvent.FetchDetails -> {
                fetchPlantWithDetails()
            }
        }
    }

    private fun fetchPlantWithDetails() {
        viewModelScope.launch {
            _plantState.update { it.copy(isLoading = true) }
            try {
                if (plantId.isNotEmpty()) {
                    val apiPlant = repositoryInteractor.getPlantDetailsById(plantId.toInt())
                    apiPlant?.let { plant ->
                        _plantState.update {
                            it.copy(
                                plant = plant,
                                isLoading = false
                            )
                        }
                        generateAiDescription()
                        generateGoogleMapsLocations()
                    }
                }
            } catch (e: Exception) {
                _plantState.update { it.copy(isLoading = false) }
                _uiEvent.send(UiEvent.Failure(e.toUiText()))
            }
        }
    }

    private suspend fun generateAiDescription() {
        _plantState.value.plant?.let { apiPlant ->
            val descriptionResponse = aiServiceInteractor.generateAiText(
                model = "gpt-4",
                systemPrompt = "You are a plant care expert. Provide detailed and accurate information about plants.",
                userPrompt = "Tell me about ${apiPlant.name} plant care.",
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

    private suspend fun generateGoogleMapsLocations() {
        _plantState.value.plant?.let { apiPlant ->
            val locationResponse = aiServiceInteractor.generateAiText(
                model = "gpt-4",
                systemPrompt = "You are a plant store locator in Hungary. Respond in JSON: [{\"name\": \"Store Name\", \"address\": \"Store Address\", \"latitude\": 47.4, \"longitude\": 19.0}]",
                userPrompt = "Find me 3 places where I can buy a(n) ${apiPlant.name} plant.",
                maxtoken = 50
            )
            _plantState.update { state ->
                state.copy(
                    locations = locationResponse.fold(
                        onSuccess = { response ->
                            val cleanedResponse = response.replace("```json", "").replace("```", "").trim()
                            if (cleanedResponse.startsWith("[")) {
                                Log.d("Location response", "Success: $cleanedResponse")
                                Gson().fromJson(
                                    cleanedResponse,
                                    object : TypeToken<List<GoogleMapsLocation>>() {}.type
                                )
                            } else {
                                Log.e("Location response", "Unexpected response format: $cleanedResponse")
                                emptyList()
                            }
                        },
                        onFailure = { exception ->
                            Log.e("Location response", "Failure: ${exception.message}", exception)
                            emptyList()
                        }
                    )
                )
            }
        }
    }
}



sealed class ApiPlantDetailsEvent {
    object FetchDetails: ApiPlantDetailsEvent()
}