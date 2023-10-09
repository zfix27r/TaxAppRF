package com.taxapprf.data.remote.firebase.dao

import com.taxapprf.data.remote.firebase.entity.FirebaseAccountEntity

interface RemoteAccountDao {
    suspend fun getAll(): List<FirebaseAccountEntity>
    suspend fun updateAll(accountModels: Map<String, FirebaseAccountEntity?>)
}