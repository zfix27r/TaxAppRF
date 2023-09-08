package com.taxapprf.data.remote.firebase.dao

import com.taxapprf.data.remote.firebase.model.FirebaseAccountModel
import com.taxapprf.domain.account.AccountModel
import kotlinx.coroutines.flow.Flow

interface RemoteAccountDao {
    fun observeAll(): Flow<Result<List<AccountModel>>>
    suspend fun save(firebaseAccountModel: FirebaseAccountModel)
    suspend fun saveAll(accountModels: List<AccountModel>)
    suspend fun deleteAll(accountModels: List<AccountModel>)
    suspend fun saveDefaultAccount()
}