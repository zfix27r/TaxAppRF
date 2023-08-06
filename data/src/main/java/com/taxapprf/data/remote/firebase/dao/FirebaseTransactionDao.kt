package com.taxapprf.data.remote.firebase.dao

import com.taxapprf.data.remote.firebase.model.FirebaseTransactionModel
import com.taxapprf.domain.transaction.DeleteTransactionModel
import com.taxapprf.domain.transaction.GetTransactionModel
import com.taxapprf.domain.transaction.GetTransactionsModel
import com.taxapprf.domain.transaction.SaveTransactionModel


interface FirebaseTransactionDao {
    suspend fun getTransactions(getTransactionsModel: GetTransactionsModel): List<FirebaseTransactionModel>
    suspend fun getTransaction(getTransactionModel: GetTransactionModel): FirebaseTransactionModel?
    suspend fun updateTransaction(saveTransactionModel: SaveTransactionModel)
    suspend fun addTransaction(saveTransactionModel: SaveTransactionModel)
    suspend fun deleteTransaction(deleteTransactionModel: DeleteTransactionModel)
}