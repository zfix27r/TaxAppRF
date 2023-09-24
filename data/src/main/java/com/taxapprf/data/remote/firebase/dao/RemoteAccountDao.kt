package com.taxapprf.data.remote.firebase.dao

import com.taxapprf.data.remote.firebase.model.FirebaseAccountModel

interface RemoteAccountDao {
    suspend fun getKey(): String?
    suspend fun getAll(): List<FirebaseAccountModel>
    suspend fun updateAll(accountModels: Map<String, FirebaseAccountModel?>)
}