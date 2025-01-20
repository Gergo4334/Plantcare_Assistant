package hu.bme.aut.android.plantbuddy.feature.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.bme.aut.android.plantbuddy.data.service.auth.AuthService
import hu.bme.aut.android.plantbuddy.domain.interactor.auth.AuthServiceInteractor
import hu.bme.aut.android.plantbuddy.domain.interactor.firebase.firestore.FirestoreRepositoryInteractor
import hu.bme.aut.android.plantbuddy.domain.model.user.RegisterUserState
import hu.bme.aut.android.plantbuddy.domain.usecases.IsEmailValidUseCase
import hu.bme.aut.android.plantbuddy.domain.usecases.PasswordsMatchUseCase
import hu.bme.aut.android.plantbuddy.ui.model.UiText
import hu.bme.aut.android.plantbuddy.ui.model.toUiText
import hu.bme.aut.android.plantbuddy.util.UiEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import hu.bme.aut.android.plantbuddy.R.string as StringResources

@HiltViewModel
class RegisterScreenViewModel @Inject constructor(
    private val authServiceInteractor: AuthServiceInteractor,
    private val firestoreInteractor: FirestoreRepositoryInteractor,
    private val isEmailValid: IsEmailValidUseCase,
    private val passwordsMatch: PasswordsMatchUseCase

): ViewModel() {
    private val _state = MutableStateFlow(RegisterUserState())
    val state = _state.asStateFlow()

    private val email get() = state.value.email
    private val password get() = state.value.password
    private val confirmPassword get() = state.value.confirmPassword
    private val favouritePlant get() = state.value.favouritePlant

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: RegisterUserEvent) {
        when (event) {
            is RegisterUserEvent.EmailChanged -> {
                val newEmail = event.email.trim()
                _state.update { it.copy(email = newEmail) }
            }
            is RegisterUserEvent.PasswordChanged -> {
                val newPassword = event.password.trim()
                _state.update { it.copy(password = newPassword) }
            }
            is RegisterUserEvent.ConfirmPasswordChanged -> {
                val newConfirmPassword = event.password.trim()
                _state.update { it.copy(confirmPassword = newConfirmPassword) }
            }
            is RegisterUserEvent.FavouritePlantChanged -> {
                val newFavouritePlant = event.plant.trim()
                _state.update { it.copy(favouritePlant = newFavouritePlant) }
            }
            RegisterUserEvent.PasswordVisibilityChanged -> {
                _state.update { it.copy(passwordVisibility = !state.value.passwordVisibility) }
            }
            RegisterUserEvent.ConfirmPasswordVisibilityChanged -> {
                _state.update { it.copy(confirmPasswordVisibility = !state.value.confirmPasswordVisibility) }
            }
            RegisterUserEvent.SignUp -> {
                onSignUp()
            }
        }
    }

    private fun onSignUp() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if(!isEmailValid(email)) {
                    _uiEvent.send(
                        UiEvent.Failure(UiText.StringResource(StringResources.invalid_email_error))
                    )
                } else {
                    if(!passwordsMatch(password, confirmPassword) && password.isNotBlank()) {
                        _uiEvent.send(
                            UiEvent.Failure(UiText.StringResource(StringResources.confirm_password_error))
                        )
                    } else {
                        authServiceInteractor.signUp(email, password)
                        firestoreInteractor.saveFavouritePlant(favouritePlant)
                        _uiEvent.send(UiEvent.Success)
                    }
                }
            } catch (e: Exception) {
                _uiEvent.send(UiEvent.Failure(e.toUiText()))
            }
        }
    }
}
sealed class RegisterUserEvent {
    data class EmailChanged(val email: String): RegisterUserEvent()
    data class PasswordChanged(val password: String): RegisterUserEvent()
    data class ConfirmPasswordChanged(val password: String): RegisterUserEvent()
    data class FavouritePlantChanged(val plant: String): RegisterUserEvent()
    object PasswordVisibilityChanged: RegisterUserEvent()
    object ConfirmPasswordVisibilityChanged: RegisterUserEvent()
    object SignUp: RegisterUserEvent()
}