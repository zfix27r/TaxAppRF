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

class GetTransactionUseCaseTest {
    private lateinit var getTransactionUseCase: GetTransactionUseCase
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
        getTransactionUseCase = GetTransactionUseCase(mockRepository)
    }

    @AfterEach
    fun tearDown() {
        mockitoSession.finishMocking()
    }

    @Test
    fun `execute should call getTransactionModel on repository and return the result`() = runBlocking {
        val transactionKey = "123456"
        val expectedTransaction = TransactionModel(
            transactionKey = transactionKey,
            type = "Trade",
            id = "456789",
            date = "2023-07-31",
            currency = "USD",
            rateCentralBank = 90.0,
            sum = 100.0,
            sumRub = 9000.0
        )
        Mockito.`when`(mockRepository.getTransactionModel(transactionKey)).thenReturn(flowOf(expectedTransaction))
        val result = getTransactionUseCase.execute(transactionKey).toList()
        Assertions.assertEquals(1, result.size)
        Assertions.assertEquals(expectedTransaction, result.first())
    }

    @Test
    fun `execute should handle error when getTransactionModel fails`() = runBlocking {
        val transactionKey = "987654"
        val expectedError = Exception("Failed to get transaction")
        Mockito.`when`(mockRepository.getTransactionModel(transactionKey)).thenThrow(expectedError)
        var error: Throwable? = null
        try {
            getTransactionUseCase.execute(transactionKey).toList()
        } catch (e: Throwable) {
            error = e
        }
        Assertions.assertEquals(expectedError, error)
    }
}
