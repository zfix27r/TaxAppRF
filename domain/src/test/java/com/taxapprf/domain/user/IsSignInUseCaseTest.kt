package com.taxapprf.domain.user

import com.taxapprf.domain.ActivityRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoSession
import org.mockito.quality.Strictness

class IsSignInUseCaseTest {
    private lateinit var isSignInUseCase: IsSignInUseCase
    @Mock
    private lateinit var mockRepository: ActivityRepository
    private lateinit var mockitoSession: MockitoSession
    @BeforeEach
    fun setup() {
        mockitoSession = Mockito.mockitoSession().initMocks(this).strictness(Strictness.STRICT_STUBS).startMocking()
        isSignInUseCase = IsSignInUseCase(mockRepository)
    }
    @AfterEach
    fun tearDown() {
        mockitoSession.finishMocking()
    }

    @Test
    fun `execute should call isSignIn on repository and return the result true`() = runBlocking {
        val expectedOutput = true
        `when`(mockRepository.isSignIn()).thenReturn(expectedOutput)
        val result = isSignInUseCase.execute()
        assertEquals(expectedOutput, result)
    }

    @Test
    fun `execute should call isSignIn on repository and return the result false`() = runBlocking {
        val expectedOutput = false
        `when`(mockRepository.isSignIn()).thenReturn(expectedOutput)
        val result = isSignInUseCase.execute()
        assertEquals(expectedOutput, result)
    }
}