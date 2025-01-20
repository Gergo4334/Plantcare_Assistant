package hu.bme.aut.android.plantbuddy.domain.usecases

import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class PasswordsMatchUseCaseTest {
    private val isPasswordConfirmedUseCase = PasswordsMatchUseCase()

    @Test
    fun `returns true if passwords match`() {
        val result = isPasswordConfirmedUseCase("password123", "password123")
        assertTrue(result)
    }

    @Test
    fun `returns false if passwords do not match`() {
        val result = isPasswordConfirmedUseCase("password123", "differentPassword")
        assertFalse(result)
    }
}