package com.taxapprf.data.remote.firebase.dao

import com.taxapprf.data.remote.firebase.model.FirebaseTransactionModel
import com.taxapprf.domain.transaction.TransactionModel
import kotlinx.coroutines.flow.Flow

interface RemoteTransactionDao {
    fun observeAll(
        accountKey: String,
        reportKey: String
    ): Flow<Result<List<TransactionModel>>>

    suspend fun saveAll(
        accountKey: String,
        reportKey: String,
        mapFirebaseTransactionsModel: Map<String, FirebaseTransactionModel>
    )

    suspend fun save(
        accountKey: String,
        reportKey: String,
        transactionKey: String?,
        firebaseTransactionModel: FirebaseTransactionModel
    )

    suspend fun delete(
        accountKey: String,
        reportKey: String,
        transactionKey: String
    )

    suspend fun deleteAll(
        accountKey: String,
        reportKey: String,
        mapFirebaseTransactionsModel: Map<String, FirebaseTransactionModel?>
    )
}