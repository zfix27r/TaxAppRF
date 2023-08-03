package com.taxapprf.domain.transaction
import com.taxapprf.domain.FirebaseRequestModel
import com.taxapprf.domain.TransactionRepository
import com.taxapprf.domain.transaction.DeleteTransactionUseCase
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoSession
import org.mockito.quality.Strictness

class DeleteTransactionUseCaseTest {
    private lateinit var deleteTransactionUseCase: DeleteTransactionUseCase
    @Mock
    private lateinit var mockRepository: TransactionRepository
    private lateinit var mockitoSession: MockitoSession

    @BeforeEach
    fun setUp() {
        mockitoSession = Mockito.mockitoSession()
            .initMocks(this).
            strictness(Strictness.STRICT_STUBS).startMocking()
        deleteTransactionUseCase = DeleteTransactionUseCase(mockRepository)
    }

    @AfterEach
    fun tearDown() {
        mockitoSession.finishMocking()
    }

    @Test
    fun `test successful transaction deletion`() = runBlocking {
        val requestModel = FirebaseRequestModel("your_transaction_key_here")
        `when`(mockRepository.deleteTransaction(requestModel)).thenReturn(flowOf(Unit))
        val result = deleteTransactionUseCase.execute(requestModel)
        verify(mockRepository).deleteTransaction(requestModel)
        result.collect { emittedResult ->
            assert(emittedResult == Unit)
        }
    }

    @Test
    fun `test failure in transaction deletion`() = runBlocking {
        val requestModel = FirebaseRequestModel("your_transaction_key_here")
        `when`(mockRepository.deleteTransaction(requestModel)).thenThrow(RuntimeException("Deletion failed"))
        val result = deleteTransactionUseCase.execute(requestModel)
        verify(mockRepository).deleteTransaction(requestModel)

        result.catch { exception ->
            assert(exception is RuntimeException)
            assert(exception.message == "Deletion failed")
        }.collect {
            assert(false)
        }
    }
}
