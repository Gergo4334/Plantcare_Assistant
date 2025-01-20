package hu.bme.aut.android.plantbuddy.domain.interactor.auth

import hu.bme.aut.android.plantbuddy.data.service.auth.AuthService
import hu.bme.aut.android.plantbuddy.domain.model.user.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthServiceInteractorImpl @Inject constructor(
    private val authService: AuthService
): AuthServiceInteractor {
    override suspend fun signUp(email: String, password: String) {
        authService.signUp(email, password)
    }

    override suspend fun authenticate(email: String, password: String) {
        authService.authenticate(email, password)
    }

    override suspend fun sendPasswordRecovery(email: String) {
        authService.sendRecoveryEmail(email)
    }

    override suspend fun deleteAccount() {
        authService.deleteAccount()
    }

    override fun signOut() {
        authService.signOut()
    }

    override fun isUserLoggedIn(): Boolean {
        return authService.hasUser
    }

    override fun getCurrentUserId(): String? {
        return authService.currentUserId
    }

    override fun observeCurrentUser(): Flow<User?> {
        return authService.currentUser
    }

}