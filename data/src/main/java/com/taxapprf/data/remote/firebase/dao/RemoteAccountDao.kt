package com.taxapprf.data.remote.firebase.dao

import com.taxapprf.data.remote.firebase.model.FirebaseAccountModel

interface RemoteAccountDao {
    suspend fun getKey(): String?
    suspend fun getAll(): List<FirebaseAccountModel>
    suspend fun saveAll(accountModels: Map<String, FirebaseAccountModel>)
    suspend fun deleteAll(accountModels: Map<String, FirebaseAccountModel?>)
}