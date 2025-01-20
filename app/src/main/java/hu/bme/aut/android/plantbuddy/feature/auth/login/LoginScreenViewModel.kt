package hu.bme.aut.android.plantbuddy.feature.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.bme.aut.android.plantbuddy.domain.interactor.auth.AuthServiceInteractor
import hu.bme.aut.android.plantbuddy.domain.model.user.LoginUserState
import hu.bme.aut.android.plantbuddy.domain.usecases.IsEmailValidUseCase
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
class LoginScreenViewModel @Inject constructor(
    private val authService: AuthServiceInteractor,
    private val isEmailValid: IsEmailValidUseCase
): ViewModel() {
    private val _state = MutableStateFlow(LoginUserState())
    val state = _state.asStateFlow()

    private val email get() = state.value.email
    private val password get() = state.value.password

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: LoginUserEvent) {
        when(event) {
            is LoginUserEvent.EmailChanged -> {
                val newEmail = event.email.trim()
                _state.update { it.copy(email = newEmail) }
            }
            is LoginUserEvent.PasswordChanged -> {
                val newPassword = event.password.trim()
                _state.update { it.copy(password = newPassword) }
            }
            LoginUserEvent.PasswordVisibilityChanged -> {
                _state.update { it.copy(passwordVisibility = !state.value.passwordVisibility) }
            }
            LoginUserEvent.SignIn -> {
                onSignIn()
            }
        }
    }

    private fun onSignIn() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if(email.isBlank()) {
                    _uiEvent.send(
                        UiEvent.Failure(UiText.StringResource(StringResources.blank_email_error))
                    )
                }

                if(!isEmailValid(email)) {
                    _uiEvent.send(
                        UiEvent.Failure(UiText.StringResource(StringResources.invalid_email_error))
                    )
                } else {
                    if(password.isBlank()) {
                        _uiEvent.send(
                            UiEvent.Failure(UiText.StringResource(StringResources.blank_password_error))
                        )
                    } else {
                        authService.authenticate(email, password)
                        _uiEvent.send(UiEvent.Success)
                    }
                }
            } catch (e: FirebaseAuthInvalidUserException) {
                _uiEvent.send(
                    UiEvent.Failure(UiText.StringResource(StringResources.user_not_found_error))
                )
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                _uiEvent.send(
                    UiEvent.Failure(UiText.StringResource(StringResources.invalid_credentials_error))
                )
            } catch (e: Exception) {
                _uiEvent.send(UiEvent.Failure(e.toUiText()))
            }
        }
    }
}

sealed class LoginUserEvent {
    data class EmailChanged(val email: String): LoginUserEvent()
    data class PasswordChanged(val password: String): LoginUserEvent()
    object PasswordVisibilityChanged: LoginUserEvent()
    object SignIn: LoginUserEvent()
}