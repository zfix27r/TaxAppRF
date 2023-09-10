package com.taxapprf.domain

import com.taxapprf.domain.account.AccountModel
import com.taxapprf.domain.account.SwitchAccountModel
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    fun getAccounts(): Flow<List<AccountModel>>
    fun switchAccount(switchAccountModel: SwitchAccountModel): Flow<Unit>
}