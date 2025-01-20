package hu.bme.aut.android.plantbuddy.feature

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import hu.bme.aut.android.plantbuddy.domain.interactor.auth.AuthServiceInteractor
import hu.bme.aut.android.plantbuddy.domain.usecases.IsEmailValidUseCase
import hu.bme.aut.android.plantbuddy.feature.auth.login.LoginScreenViewModel
import hu.bme.aut.android.plantbuddy.feature.auth.login.LoginUserEvent
import hu.bme.aut.android.plantbuddy.util.UiEvent
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import hu.bme.aut.android.plantbuddy.R.string as StringResources

class LoginViewModelTest {

    private lateinit var mockAuthService: AuthServiceInteractor
    private lateinit var mockEmailValidator: IsEmailValidUseCase
    private lateinit var viewModel: LoginScreenViewModel

    @Before
    fun setUp() {
        mockAuthService = mockk()
        mockEmailValidator = mockk()
        viewModel = LoginScreenViewModel(mockAuthService, mockEmailValidator)
    }

    @Test
    fun `test EmailChanged event updates email in state`() {
        viewModel.onEvent(LoginUserEvent.EmailChanged("test@example.com"))

        assertEquals("test@example.com", viewModel.state.value.email)
    }

    @Test
    fun `test onSignIn sends failure event for blank email`(): Unit = runBlocking {
        viewModel.onEvent(LoginUserEvent.EmailChanged(""))
        viewModel.onEvent(LoginUserEvent.SignIn)

        val event = viewModel.uiEvent.first()
        assertTrue(event is UiEvent.Failure)
    }

    @Test
    fun `test onSignIn sends failure event for invalid email`(): Unit = runBlocking {
        viewModel.onEvent(LoginUserEvent.EmailChanged("invalidemail"))
        viewModel.onEvent(LoginUserEvent.SignIn)

        val event = viewModel.uiEvent.first()
        assertTrue(event is UiEvent.Failure)
    }
}