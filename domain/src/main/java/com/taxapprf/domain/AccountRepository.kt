package com.taxapprf.domain

import com.taxapprf.domain.account.AccountsModel
import com.taxapprf.domain.account.SwitchAccountModel
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    fun observeAccounts(): Flow<AccountsModel>
    suspend fun syncAccounts()
    suspend fun switchAccount(switchAccountModel: SwitchAccountModel)
}