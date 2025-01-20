package hu.bme.aut.android.plantbuddy.data.service

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import hu.bme.aut.android.plantbuddy.data.service.auth.FirebaseAuthService
import hu.bme.aut.android.plantbuddy.domain.model.user.User
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class FirebaseAuthServiceTest {
    private lateinit var mockFirebaseAuth: FirebaseAuth
    private lateinit var mockAuthService: FirebaseAuthService

    @Before
    fun setUp() {
        mockFirebaseAuth = mockk(relaxed = true)
        mockAuthService = FirebaseAuthService(mockFirebaseAuth)
    }

    @Test
    fun `currentUserId should return null when no user is logged in`() {
        every { mockFirebaseAuth.currentUser } returns null

        val userId = mockAuthService.currentUserId

        assertNull(userId)
    }

    @Test
    fun `hasUser should return true when a user is logged in`() {
        every { mockFirebaseAuth.currentUser } returns mockk()

        val hasUser = mockAuthService.hasUser

        assertTrue(hasUser)
    }

    @Test
    fun `authenticate should call signInWithEmailAndPassword`() = runTest {
        val email = "test@example.com"
        val password = "password"

        val mockTask = mockk<Task<AuthResult>>()
        coEvery { mockTask.await() } returns mockk(relaxed = true)
        every { mockFirebaseAuth.signInWithEmailAndPassword(email, password) } returns mockTask

        mockAuthService.authenticate(email, password)

        coVerify { mockFirebaseAuth.signInWithEmailAndPassword(email, password) }
    }

    @Test
    fun `currentUser Flow should emit User when state changes`() = runTest {
        val authStateListenerSlot = slot<FirebaseAuth.AuthStateListener>()

        val user = mockk<FirebaseUser> {
            every { uid } returns "123"
            every { email } returns "test@example.com"
        }

        every { mockFirebaseAuth.addAuthStateListener(capture(authStateListenerSlot)) } answers {
            authStateListenerSlot.captured.onAuthStateChanged(mockFirebaseAuth)
        }
        every { mockFirebaseAuth.currentUser } returns user

        val flow = mockAuthService.currentUser
        val emissions = flow.take(1).toList()

        assertEquals(1, emissions.size)
        assertEquals(User("123", "test@example.com"), emissions.first())
    }

    @Test
    fun `authenticate should throw exception on invalid credentials`() = runTest {
        val email = "wrong@example.com"
        val password = "wrongpassword"

        val mockTask = mockk<Task<AuthResult>>(relaxed = true)

        coEvery { mockTask.await() } throws FirebaseAuthException("ERROR_WRONG_PASSWORD", "Invalid password")

        every { mockFirebaseAuth.signInWithEmailAndPassword(email, password) } returns mockTask

        try {
            mockAuthService.authenticate(email, password)
            fail("Expected FirebaseAuthException to be thrown")
        } catch (exception: FirebaseAuthException) {
            assertEquals("ERROR_WRONG_PASSWORD", exception.errorCode)
        }

        coVerify { mockFirebaseAuth.signInWithEmailAndPassword(email, password) }
    }

}