package com.taxapprf.domain.account

import com.taxapprf.domain.ActivityRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoSession
import org.mockito.quality.Strictness

class GetAccountsUseCaseTest {
    private lateinit var getAccountsUseCase: GetAccountsUseCase
    @Mock
    private lateinit var mockRepository: ActivityRepository
    private lateinit var mockitoSession: MockitoSession
    @BeforeEach
    fun setup() {
        mockitoSession = Mockito.mockitoSession()
            .initMocks(this).
            strictness(Strictness.STRICT_STUBS).startMocking()
        getAccountsUseCase = GetAccountsUseCase(mockRepository)
    }

    @Test
    fun `execute should return empty list when repository returns empty`() = runBlocking(){
        val emptyListFlow: Flow<List<AccountModel>> = flowOf(emptyList())
        `when`(mockRepository.getAccounts()).thenReturn(emptyListFlow)
        val resultFlow: Flow<List<AccountModel>> = getAccountsUseCase.execute()
        resultFlow.toList().also { result ->
            assertEquals(emptyList<AccountModel>(), result)
        }
    }

    @Test
    fun `execute should return a list of accounts when repository returns data`() = runBlocking {
        val accountList = listOf(
            AccountModel("Account1", true),
            AccountModel("Account2", false),
            AccountModel("Account3", true)
        )
        val accountListFlow: Flow<List<AccountModel>> = flowOf(accountList)
        `when`(mockRepository.getAccounts()).thenReturn(accountListFlow)
        val resultFlow: Flow<List<AccountModel>> = getAccountsUseCase.execute()
        resultFlow.toList().also { result ->
            assertEquals(accountList, result)
        }
    }
}
