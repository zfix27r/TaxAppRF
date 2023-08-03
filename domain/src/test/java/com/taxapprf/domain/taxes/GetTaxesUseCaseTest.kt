package com.taxapprf.domain.taxes

import com.taxapprf.domain.TaxesRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.quality.Strictness
import org.mockito.MockitoSession

class GetTaxesUseCaseTest {
    private lateinit var getTaxesUseCase: GetTaxesUseCase

    @Mock
    private lateinit var mockRepository: TaxesRepository
    private lateinit var mockitoSession: MockitoSession

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        mockitoSession = Mockito.mockitoSession()
            .initMocks(this)
            .strictness(Strictness.STRICT_STUBS)
            .startMocking()
        getTaxesUseCase = GetTaxesUseCase(mockRepository)
    }

    @AfterEach
    fun tearDown() {
        mockitoSession.finishMocking()
    }

    @Test
    fun `execute should call getTaxes on repository and return the result`() = runBlocking {
        val taxesData = listOf(
            TaxesAdapterModel("2023", "1000"),
            TaxesAdapterModel("2024", "1200")
        )
        Mockito.`when`(mockRepository.getTaxes()).thenReturn(flowOf(taxesData))
        val result = getTaxesUseCase.execute().toList()
        Assertions.assertEquals(taxesData, result)
    }

    @Test
    fun `execute should handle error when getTaxes fails`() = runBlocking {
        val expectedError = Exception("Failed to get taxes")
        Mockito.`when`(mockRepository.getTaxes()).thenThrow(expectedError)
        var error: Throwable? = null
        try {
            getTaxesUseCase.execute().toList()
        } catch (e: Throwable) {
            error = e
        }
        assertEquals(expectedError, error)
    }

    @Test
    fun `execute should return empty list when no taxes available`() = runBlocking {
        Mockito.`when`(mockRepository.getTaxes()).thenReturn(flowOf(emptyList()))
        val result = getTaxesUseCase.execute().toList()
        assertTrue(result.isEmpty())
    }
}
