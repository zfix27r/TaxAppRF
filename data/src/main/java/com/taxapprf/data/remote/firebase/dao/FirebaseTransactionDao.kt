package com.taxapprf.data.remote.firebase.dao

import com.taxapprf.data.remote.firebase.model.FirebaseTransactionModel
import com.taxapprf.domain.transaction.DeleteTransactionModel
import com.taxapprf.domain.transaction.SaveTransactionModel


interface FirebaseTransactionDao {
    suspend fun getTransactions(accountKey: String, reportKey: String): List<FirebaseTransactionModel>
    suspend fun updateTransaction(transaction: SaveTransactionModel)
    suspend fun addTransaction(transaction: SaveTransactionModel)
    suspend fun deleteTransaction(deleteTransactionModel: DeleteTransactionModel)
}