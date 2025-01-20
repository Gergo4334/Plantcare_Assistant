package hu.bme.aut.android.plantbuddy.domain.model.plant.state

import hu.bme.aut.android.plantbuddy.data.remote.api.ai.kindwise.PlantIdentificationResponse
import hu.bme.aut.android.plantbuddy.domain.model.plant.api.ApiPlant
import hu.bme.aut.android.plantbuddy.domain.model.plant.filter.ApiPlantFilterButtonState


data class ApiPlantListState (
    val plantList: List<ApiPlant> = emptyList(),
    val filteredList: List<ApiPlant> = emptyList(),
    val isLoading: Boolean = false,
    var currentPage: Int = 1,
    val searchText: String = "",
    val filterButtonState: ApiPlantFilterButtonState = ApiPlantFilterButtonState.NAME,
    val uriForImageRecognition: String? = null,
    val recognitionResponseName: String = "",
    val recognitionResponseImage: String? = null,
    val recognitionResponseProbability: Float = 0f,
    val showResultDialog: Boolean = false,
    val aiDescription: String = ""
)