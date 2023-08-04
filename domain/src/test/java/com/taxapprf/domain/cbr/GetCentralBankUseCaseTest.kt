package com.taxapprf.domain.cbr

import com.taxapprf.domain.TaxesRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoSession
import org.mockito.quality.Strictness

class GetRateCentralBankUseCaseTest {
    private lateinit var getRateCentralBankUseCase: GetRateCentralBankUseCase
    @Mock
    private lateinit var mockRepository: TaxesRepository
    private lateinit var mockitoSession: MockitoSession
    @BeforeEach
    fun setup() {
        mockitoSession = Mockito.mockitoSession()
            .initMocks(this)
            .strictness(Strictness.STRICT_STUBS)
            .startMocking()
        getRateCentralBankUseCase = GetRateCentralBankUseCase(mockRepository)
    }

    @Test
    fun `execute should call getCBRRate on repository and return the result`() = runBlocking {
        val date = "2023-07-31"
        val currency = "USD"
        val expectedRate = 90.0
        `when`(mockRepository.getCBRRate(date, currency)).thenReturn(flowOf(expectedRate))
        val result = getRateCentralBankUseCase.execute(date, currency).toList()
        assertEquals(1, result.size)
        assertEquals(expectedRate, result.first())
    }

    @Test
    fun `execute should handle error when getCBRRate fails`() = runBlocking {
        val date = "2023-07-31"
        val currency = "EUR"
        val expectedError = Exception("Failed to fetch rate from central bank")
        `when`(mockRepository.getCBRRate(date, currency)).thenThrow(expectedError)
        var error: Throwable? = null
        try {
            getRateCentralBankUseCase.execute(date, currency).toList()
        } catch (e: Throwable) {
            error = e
        }
        assertEquals(expectedError, error)
    }
}
