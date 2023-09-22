package com.taxapprf.domain

import com.taxapprf.domain.account.AccountsModel
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    fun observeAccounts(): Flow<AccountsModel>
    suspend fun syncAccounts()
    suspend fun switchAccount(accountId: Int)
}