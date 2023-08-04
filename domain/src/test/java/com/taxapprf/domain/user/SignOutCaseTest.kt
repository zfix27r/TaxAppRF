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
import org.mockito.MockitoAnnotations
import org.mockito.MockitoSession
import org.mockito.quality.Strictness

class SignOutUseCaseTest {
    private lateinit var signOutUseCase: SignOutUseCase
    @Mock
    private lateinit var mockRepository: ActivityRepository
    private lateinit var mockitoSession: MockitoSession
    @BeforeEach
    fun setup() {
        mockitoSession = Mockito.mockitoSession().initMocks(this).strictness(Strictness.STRICT_STUBS).startMocking()
        signOutUseCase = SignOutUseCase(mockRepository)
    }
    @AfterEach
    fun tearDown() {
        mockitoSession.finishMocking()
    }

    @Test
    fun `execute should call signOut on repository and return the result`() = runBlocking {
        val expectedOutput = Unit
        `when`(mockRepository.signOut()).thenReturn(flowOf(expectedOutput))
        val result = signOutUseCase.execute().toList()
        Assertions.assertTrue(result.isNotEmpty())
        Assertions.assertTrue(result.first() == expectedOutput)
    }
}
