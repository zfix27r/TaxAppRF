package com.taxapprf.domain.year

import com.taxapprf.domain.FirebaseRequestModel
import com.taxapprf.domain.TransactionRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.quality.Strictness
import org.mockito.MockitoSession

class GetYearSumUseCaseTest {
    private lateinit var getYearSumUseCase: GetYearSumUseCase
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
        getYearSumUseCase = GetYearSumUseCase(mockRepository)
    }

    @AfterEach
    fun tearDown() {
        mockitoSession.finishMocking()
    }

    @Test
    fun `execute should call getYearSum on repository and return the result`() = runBlocking {
        val requestModel = FirebaseRequestModel(account = "some_account", year = "2023","3")
        val expectedSum = 5000.0
        Mockito.`when`(mockRepository.getYearSum(requestModel)).thenReturn(flowOf(expectedSum))
        val result = getYearSumUseCase.execute(requestModel).toList()
        Assertions.assertEquals(1, result.size)
        assertEquals(expectedSum, result.first())
    }

    @Test
    fun `execute should handle error when getYearSum fails`() = runBlocking {
        val requestModel = FirebaseRequestModel(account = "some_account", year = "2024","2")
        val expectedError = Exception("Failed to get year sum")
        Mockito.`when`(mockRepository.getYearSum(requestModel)).thenThrow(expectedError)
        var error: Throwable? = null
        try {
            getYearSumUseCase.execute(requestModel).toList()
        } catch (e: Throwable) {
            error = e
        }
        assertEquals(expectedError, error)
    }
}
