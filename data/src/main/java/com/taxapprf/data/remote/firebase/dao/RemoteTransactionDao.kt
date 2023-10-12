package com.taxapprf.data.remote.firebase.dao

import com.taxapprf.data.remote.firebase.entity.FirebaseTransactionEntity

interface RemoteTransactionDao {
    suspend fun getKey(
        accountKey: String,
        reportKey: String
    ): String?

    suspend fun getAll(
        accountKey: String,
        reportKey: String
    ): List<FirebaseTransactionEntity>

    suspend fun deleteAllTransactions(
        accountKey: String,
        reportKey: String
    )

    suspend fun updateTransactions(
        accountKey: String,
        reportKey: String,
        transactionsModels: Map<String, FirebaseTransactionEntity?>
    )
}