package com.taxapprf.domain.user

import com.taxapprf.domain.ActivityRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoSession
import org.mockito.quality.Strictness

class SignUpUseCaseTest {
    private lateinit var signUpUseCase: SignUpUseCase
    @Mock
    private lateinit var mockRepository: ActivityRepository
    private lateinit var mockitoSession: MockitoSession
    @BeforeEach
    fun setup() {
        mockitoSession = Mockito.mockitoSession().initMocks(this
        ).strictness(Strictness.STRICT_STUBS).startMocking()
        signUpUseCase = SignUpUseCase(mockRepository)
    }
    @AfterEach
    fun tearDown() {
        mockitoSession.finishMocking()
    }

    @Test
    fun `execute should call signUp on repository and return the result`() = runBlocking {
        val signUpModel = SignUpModel("", "john@example.com", "password", "1234567890")
        val expectedOutput = Unit
        `when`(mockRepository.signUp(signUpModel)).thenReturn(flowOf(expectedOutput))
        val result = signUpUseCase.execute(signUpModel).toList()
        Assertions.assertEquals(1, result.size)
        assertEquals(expectedOutput, result.first())
    }

    @Test
    fun `execute should handle error when signUp fails`() = runBlocking {
        val signUpModel = SignUpModel("", "john@example.com", "password", "1234567890")
        val expectedError = Exception("Sign-up failed")
        `when`(mockRepository.signUp(signUpModel)).thenThrow(expectedError)
        var error: Throwable? = null
        try {
            signUpUseCase.execute(signUpModel).toList()
        } catch (e: Throwable) {
            error = e
        }
        assertEquals(expectedError, error)
    }
}