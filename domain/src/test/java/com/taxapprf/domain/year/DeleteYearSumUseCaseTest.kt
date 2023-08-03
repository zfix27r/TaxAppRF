package com.taxapprf.domain.year

import com.taxapprf.domain.FirebaseRequestModel
import com.taxapprf.domain.TransactionRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.quality.Strictness
import org.mockito.MockitoSession

class DeleteYearSumUseCaseTest {
    private lateinit var deleteYearSumUseCase: DeleteYearSumUseCase
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
        deleteYearSumUseCase = DeleteYearSumUseCase(mockRepository)
    }

    @AfterEach
    fun tearDown() {
        mockitoSession.finishMocking()
    }

    @Test
    fun `execute should call deleteYearSum on repository and complete successfully`() = runBlocking {
        val requestModel = FirebaseRequestModel(account = "some_account", year = "2023", "3")
        Mockito.`when`(mockRepository.deleteYearSum(requestModel)).thenReturn(flowOf(Unit))
        val result = deleteYearSumUseCase.execute(requestModel).toList()
        assertEquals(1, result.size)
        assertEquals(Unit, result.first())
    }

    @Test
    fun `execute should handle error when deleteYearSum fails`() = runBlocking {
        val requestModel = FirebaseRequestModel(account = "some_account", year = "2024","1")
        val expectedError = Exception("Failed to delete year sum")
        Mockito.`when`(mockRepository.deleteYearSum(requestModel)).thenThrow(expectedError)
        var error: Throwable? = null
        try {
            deleteYearSumUseCase.execute(requestModel).toList()
        } catch (e: Throwable) {
            error = e
        }
        assertEquals(expectedError, error)
    }
}
