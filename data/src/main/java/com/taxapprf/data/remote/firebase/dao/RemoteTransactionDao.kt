package com.taxapprf.data.remote.firebase.dao

import com.taxapprf.data.remote.firebase.model.FirebaseTransactionModel

interface RemoteTransactionDao {
    suspend fun getKey(
        accountKey: String,
        reportKey: String
    ): String?

    suspend fun getAll(
        accountKey: String,
        reportKey: String
    ): List<FirebaseTransactionModel>

    suspend fun updateAll(
        accountKey: String,
        reportKey: String,
        transactionsModels: Map<String, FirebaseTransactionModel?>
    )
}