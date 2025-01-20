package hu.bme.aut.android.plantbuddy.domain.model.user


data class LoginUserState(
    val email: String = "",
    val password: String = "",
    val passwordVisibility: Boolean = false
)