package com.taxapprf.domain.user

import com.taxapprf.domain.ActivityRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoSession
import org.mockito.quality.Strictness

class SignInUseCaseTest {
    private lateinit var signInUseCase: SignInUseCase
    @Mock
    private lateinit var mockRepository: ActivityRepository
    private lateinit var mockitoSession: MockitoSession

    @BeforeEach
    fun setup() {
        mockitoSession = Mockito.mockitoSession()
            .initMocks(this)
            .strictness(Strictness.STRICT_STUBS)
            .startMocking()
        signInUseCase = SignInUseCase(mockRepository)
    }
    @AfterEach
    fun tearDown() {
        mockitoSession.finishMocking()
    }

    @Test
    fun `execute should call signIn on repository and return the result`() = runBlocking {
        val signInModel = SignInModel("test@example.com", "password")
        val expectedOutput = Unit
        `when`(mockRepository.signIn(signInModel)).thenReturn(flowOf(expectedOutput))
        val result = signInUseCase.execute(signInModel).toList()
        Assertions.assertEquals(1, result.size)
        Assertions.assertEquals(expectedOutput, result.first())
    }
    @Test
    fun `execute should handle error when signIn fails`() = runBlocking {
        val signInModel = SignInModel("test@example.com", "password")
        val expectedError = Exception("Sign-in failed")
        `when`(mockRepository.signIn(signInModel)).thenThrow(expectedError)
        var error: Throwable? = null
        try {
            signInUseCase.execute(signInModel).toList()
        } catch (e: Throwable) {
            error = e
        }
        Assertions.assertEquals(expectedError, error)
    }

    @Test
    fun `execute should handle empty email and password`() = runBlocking {
        val signInModel = SignInModel("", "")
        val expectedOutput = Unit
        `when`(mockRepository.signIn(signInModel)).thenReturn(flowOf(expectedOutput))
        val result = signInUseCase.execute(signInModel).toList()
        Assertions.assertEquals(1, result.size)
        Assertions.assertEquals(expectedOutput, result.first())
    }
}