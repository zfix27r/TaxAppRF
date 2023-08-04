package com.taxapprf.domain.year

import com.taxapprf.domain.TransactionRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.quality.Strictness
import org.mockito.MockitoSession

class SaveYearSumUseCaseTest {
    private lateinit var saveYearSumUseCase: SaveYearSumUseCase
    @Mock
    private lateinit var mockRepository: TransactionRepository
    private lateinit var mockitoSession: MockitoSession

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        mockitoSession = Mockito.mockitoSession()
            .initMocks(this)
            .strictness(Strictness.STRICT_STUBS)
            .startMocking()
        saveYearSumUseCase = SaveYearSumUseCase(mockRepository)
    }

    @AfterEach
    fun tearDown() {
        mockitoSession.finishMocking()
    }

    @Test
    fun `execute should call saveYearSum on repository and complete successfully`() = runBlocking {
        val saveYearSumModel = SaveYearSumModel("some_account", "2023", 5000.0)
        Mockito.`when`(mockRepository.saveYearSum(saveYearSumModel)).thenReturn(flowOf(Unit))
        val result = saveYearSumUseCase.execute(saveYearSumModel).toList()
        assertEquals(1, result.size)
        assertEquals(Unit, result.first())
    }

    @Test
    fun `execute should handle error when saveYearSum fails`() = runBlocking {
        val saveYearSumModel = SaveYearSumModel("some_account", "2024", 0.00)
        val expectedError = Exception("Failed to save year sum")
        Mockito.`when`(mockRepository.saveYearSum(saveYearSumModel)).thenThrow(expectedError)
        var error: Throwable? = null
        try {
            saveYearSumUseCase.execute(saveYearSumModel).toList()
        } catch (e: Throwable) {
            error = e
        }
        assertEquals(expectedError, error)
    }

    @Test
    fun `execute should handle invalid saveYearSumModel`() = runBlocking {
        val saveYearSumModel = SaveYearSumModel("", "2025", 7000.0)
        Mockito.`when`(mockRepository.saveYearSum(saveYearSumModel)).thenThrow(IllegalArgumentException())
        var error: Throwable? = null
        try {
            saveYearSumUseCase.execute(saveYearSumModel).toList()
        } catch (e: Throwable) {
            error = e
        }
        assertTrue(error is IllegalArgumentException)
    }
}
