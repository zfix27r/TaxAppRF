package com.taxapprf.domain

import com.taxapprf.domain.user.AccountModel
import com.taxapprf.domain.user.SaveAccountModel
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    fun getAccounts(): Flow<List<AccountModel>>
    fun saveAccount(saveAccountModel: SaveAccountModel): Flow<Unit>
}