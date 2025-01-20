package hu.bme.aut.android.plantbuddy.feature.home.profile

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.bme.aut.android.plantbuddy.domain.interactor.auth.AuthServiceInteractor
import hu.bme.aut.android.plantbuddy.domain.interactor.firebase.firestore.FirestoreRepositoryInteractor
import hu.bme.aut.android.plantbuddy.domain.interactor.firebase.storage.FirebaseStorageServiceInteractor
import hu.bme.aut.android.plantbuddy.domain.model.user.ProfileState
import hu.bme.aut.android.plantbuddy.ui.model.toUiText
import hu.bme.aut.android.plantbuddy.util.UiEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(
    private val authServiceInteractor: AuthServiceInteractor,
    private val firestoreInteractor: FirestoreRepositoryInteractor,
    private val storageInteractor: FirebaseStorageServiceInteractor
): ViewModel() {
    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()
    var successMessage = mutableStateOf("")

    fun onEvent(event: ProfileScreenEvent) {
        when (event) {
            ProfileScreenEvent.FetchUser -> {
                fetchUserData()
            }
            ProfileScreenEvent.FetchImages -> {
                fetchPlantData()
            }
        }
    }

    private fun fetchUserData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = authServiceInteractor.observeCurrentUser().firstOrNull()
                if (user != null) {
                    val username = user.email
                    val favouritePlant = firestoreInteractor.getFavouritePlant()
                    _state.update {
                        it.copy(username = username ?: "-", favouritePlant = favouritePlant ?: "-")
                    }
                }
            } catch (e: Exception) {
                _uiEvent.send(UiEvent.Failure(e.toUiText()))
            }
        }
    }

    private fun fetchPlantData() {
        viewModelScope.launch(Dispatchers.IO) {
            firestoreInteractor.getPlants()
                .onStart {
                    _state.update { it.copy(isImagesLoading = true) }
                }
                .catch { exception ->
                    _uiEvent.send(UiEvent.Failure(exception.toUiText()))
                    _state.update { it.copy(isImagesLoading = false) }
                }
                .collect { plants ->
                    val imageMap = mutableMapOf<String, List<String>>()
                    plants.forEach { plant ->
                        val imageResult = storageInteractor.getImagesForPlant(plant.id)
                        if (imageResult.isSuccess) {
                            imageMap[plant.id] = imageResult.getOrDefault(emptyList())
                        }
                    }
                    _state.update { it.copy(images = imageMap, isImagesLoading = false) }
                    Log.d("ProfileScreenViewModel", "Collected images: $imageMap")
                }
        }
    }
}

sealed class ProfileScreenEvent() {
    object FetchUser: ProfileScreenEvent()
    object FetchImages: ProfileScreenEvent()
}