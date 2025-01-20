package hu.bme.aut.android.plantbuddy.domain.interactor.auth

import hu.bme.aut.android.plantbuddy.domain.model.user.User
import kotlinx.coroutines.flow.Flow

interface AuthServiceInteractor {
    suspend fun signUp(email: String, password: String)
    suspend fun authenticate(email: String, password: String)
    suspend fun sendPasswordRecovery(email: String)
    suspend fun deleteAccount()
    fun signOut()
    fun isUserLoggedIn(): Boolean
    fun getCurrentUserId(): String?
    fun observeCurrentUser(): Flow<User?>
}