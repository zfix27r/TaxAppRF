package com.taxapprf.domain.transaction

import com.taxapprf.domain.TransactionRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.quality.Strictness
import org.mockito.MockitoSession

class SaveTransactionUseCaseTest {
    private lateinit var saveTransactionUseCase: SaveTransactionUseCase
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
        saveTransactionUseCase = SaveTransactionUseCase(mockRepository)
    }

    @AfterEach
    fun tearDown() {
        mockitoSession.finishMocking()
    }

    @Test
    fun `execute should call saveTransactionModel on repository and complete successfully`() = runBlocking {
        val saveTransactionModel = com.taxapprf.taxapp.ui.transactions.detail.SaveTransactionModel(
            id = "101",
            type = TransactionType.TRADE.toString(),
            date = "2023-07-31",
            currency = "USD",
            rateCentralBank = 75.0,
            sum = 100.0,
            sumRub = 7500.0
        )
        Mockito.`when`(mockRepository.saveTransactionModel(saveTransactionModel)).thenReturn(flowOf(Unit))
        val result = saveTransactionUseCase.execute(saveTransactionModel).toList()
        Assertions.assertEquals(1, result.size)
        Assertions.assertEquals(Unit, result.first())
    }

    @Test
    fun `execute should handle error when saveTransaction fails because of no date`() = runBlocking {
        val saveTransactionModel = com.taxapprf.taxapp.ui.transactions.detail.SaveTransactionModel(
            id = "202",
            type = TransactionType.COMMISSION.toString(),
            date = "",
            currency = "EUR",
            rateCentralBank = 100.0,
            sum = 150.0,
            sumRub = 15000.0
        )
        val expectedError = Exception("Failed to save transaction")
        Mockito.`when`(mockRepository.saveTransactionModel(saveTransactionModel)).thenThrow(expectedError)
        var error: Throwable? = null
        try {
            saveTransactionUseCase.execute(saveTransactionModel).toList()
        } catch (e: Throwable) {
            error = e
        }
        Assertions.assertEquals(expectedError, error)
    }
}
