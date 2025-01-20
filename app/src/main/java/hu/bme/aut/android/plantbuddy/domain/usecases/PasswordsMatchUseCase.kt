package hu.bme.aut.android.plantbuddy.domain.usecases

class PasswordsMatchUseCase {
    operator fun invoke(password: String, confirmPassword: String): Boolean =
        password == confirmPassword
}