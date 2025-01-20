package hu.bme.aut.android.plantbuddy.feature.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.bme.aut.android.plantbuddy.domain.interactor.auth.AuthServiceInteractor
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val authService: AuthServiceInteractor
): ViewModel() {
    fun isUserLoggedIn(): Boolean {
        val loggedIn = authService.isUserLoggedIn()
        Log.d("SplashScreenViewModel", "Is user logged in: $loggedIn")
        return loggedIn
    }
}