package hu.bme.aut.android.plantbuddy.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.bme.aut.android.plantbuddy.domain.interactor.auth.AuthServiceInteractor
import hu.bme.aut.android.plantbuddy.ui.model.toUiText
import hu.bme.aut.android.plantbuddy.util.UiEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val authService: AuthServiceInteractor
): ViewModel() {
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()
    fun onEvent(event: HomeScreenEvent) {
        when(event) {
            HomeScreenEvent.SignOut -> {
                onSignOut()
            }
        }
    }

    private fun onSignOut() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                authService.signOut()
                _uiEvent.send(UiEvent.Success)
            } catch (e: Exception) {
                _uiEvent.send(UiEvent.Failure(e.toUiText()))
            }
        }
    }
}

sealed class HomeScreenEvent {
    object SignOut: HomeScreenEvent()
}