package com.taxapprf.data.remote.firebase.dao

import com.taxapprf.data.remote.firebase.model.FirebaseTransactionModel
import com.taxapprf.domain.transaction.DeleteTransactionModel
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.transaction.TransactionModel
import kotlinx.coroutines.flow.Flow


interface FirebaseTransactionDao {
    fun observeTransactions(
        accountKey: String,
        reportKey: String
    ): Flow<Result<List<TransactionModel>>>

    suspend fun saveTransaction(saveTransactionModel: SaveTransactionModel)
    suspend fun saveTransactions(
        accountKey: String,
        yearKey: String,
        transactionModels: Map<String, FirebaseTransactionModel>
    )

    suspend fun deleteTransaction(deleteTransactionModel: DeleteTransactionModel)
}