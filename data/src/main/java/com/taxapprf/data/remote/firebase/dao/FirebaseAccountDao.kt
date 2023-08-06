package com.taxapprf.data.remote.firebase.dao

import com.taxapprf.data.remote.firebase.model.FirebaseAccountModel


interface FirebaseAccountDao {
    suspend fun getAccounts(): List<FirebaseAccountModel>
    suspend fun saveAccount(firebaseAccountModel: FirebaseAccountModel)
    suspend fun saveDefaultAccount()
}