package hu.bme.aut.android.plantbuddy.domain.model.user


data class RegisterUserState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val passwordVisibility: Boolean = false,
    val confirmPasswordVisibility: Boolean = false,
    val favouritePlant: String = ""
)