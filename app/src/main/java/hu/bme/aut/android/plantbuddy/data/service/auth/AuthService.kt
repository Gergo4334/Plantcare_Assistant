package hu.bme.aut.android.plantbuddy.data.service.auth

import hu.bme.aut.android.plantbuddy.domain.model.user.User
import kotlinx.coroutines.flow.Flow


interface AuthService {
    val currentUserId: String?
    val hasUser: Boolean
    val currentUser: Flow<User?>

    suspend fun signUp(
        email: String,
        password: String,
    )

    suspend fun authenticate(
        email: String,
        password: String,
    )

    suspend fun sendRecoveryEmail(
        email: String
    )

    suspend fun deleteAccount()

    fun signOut()
}