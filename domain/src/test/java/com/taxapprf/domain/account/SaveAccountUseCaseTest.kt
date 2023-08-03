package com.taxapprf.domain.account

import com.taxapprf.domain.ActivityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoSession
import org.mockito.quality.Strictness


class SaveAccountUseCaseTest {

    private lateinit var saveAccountUseCase: SaveAccountUseCase
    @Mock
    private lateinit var mockRepository: ActivityRepository
    private lateinit var mockitoSession: MockitoSession

    @BeforeEach
    fun setup() {
        mockitoSession = Mockito.mockitoSession()
            .initMocks(this)
            .strictness(Strictness.STRICT_STUBS)
            .startMocking()

        saveAccountUseCase = SaveAccountUseCase(mockRepository)
    }

    @Test
    fun `execute should save the account in the repository`() = runBlocking {
        val accountModel = AccountModel("", true)
        val expectedFlow: Flow<Unit> = flowOf(Unit)
        Mockito.`when`(mockRepository.saveAccount(accountModel)).thenReturn(expectedFlow)
        val resultFlow: Flow<Unit> = saveAccountUseCase.execute(accountModel)
        assertEquals(expectedFlow.toList(), resultFlow.toList())
    }

    @Test
    fun `execute should return an empty flow when saving account in the repository`() = runBlocking {
        val accountModel = AccountModel("Test Account", true)
        val emptyFlow: Flow<Unit> = flowOf()
        Mockito.`when`(mockRepository.saveAccount(accountModel)).thenReturn(emptyFlow)
        val resultFlow: Flow<Unit> = saveAccountUseCase.execute(accountModel)
        assertEquals(emptyFlow.toList(), resultFlow.toList())
    }
}
