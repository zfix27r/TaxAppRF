package com.taxapprf.domain.transaction

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

class GetTransactionsUseCaseTest {
    private lateinit var getTransactionsUseCase: ObserveTransactionsUseCase
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
        getTransactionsUseCase = ObserveTransactionsUseCase(mockRepository)
    }

    @AfterEach
    fun tearDown() {
        mockitoSession.finishMocking()
    }

    @Test
    fun `execute should call getTransactionModels on repository and return the result`() = runBlocking {
        val year = "2023"
        val transactions = listOf(
            TransactionModel("1", "Trade", "101", "2023-01-01", "USD", 90.0, 100.0, 9000.0),
            TransactionModel("2", "Commission", "202", "2023-02-02", "EUR", 100.0, 150.0, 15000.0)
        )
        Mockito.`when`(mockRepository.getTransactionModels(year)).thenReturn(flowOf(transactions))
        val result = getTransactionsUseCase.execute(year).toList()
        assertEquals(1, result.size)
        assertEquals(transactions, result.first())
    }

    @Test
    fun `execute should return empty list when no transactions available`() = runBlocking {
        val year = "2025"
        Mockito.`when`(mockRepository.getTransactionModels(year)).thenReturn(flowOf(emptyList()))
        val result = getTransactionsUseCase.execute(year).toList()
        assertTrue(result.isEmpty())
    }
}
