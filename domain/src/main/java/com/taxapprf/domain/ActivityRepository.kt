package com.taxapprf.domain

import com.taxapprf.domain.activity.AccountModel
import kotlinx.coroutines.flow.Flow

interface ActivityRepository {
    fun getAccountModel(): Flow<AccountModel?>
}