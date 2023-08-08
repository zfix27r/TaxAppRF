package com.taxapprf.domain

import com.taxapprf.domain.account.AccountModel
import com.taxapprf.domain.account.SaveAccountModel
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    fun getAccounts(): Flow<List<AccountModel>>
    fun saveAccount(saveAccountModel: SaveAccountModel): Flow<Unit>
    fun changeAccount(oldAccountModel: AccountModel, newAccountModel: AccountModel): Flow<Unit>
}