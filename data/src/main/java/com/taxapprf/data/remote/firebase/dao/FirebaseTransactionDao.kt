package com.taxapprf.data.remote.firebase.dao

import com.taxapprf.domain.transaction.DeleteTransactionModel
import com.taxapprf.domain.transaction.GetTransactionsModel
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.transaction.TransactionModel
import kotlinx.coroutines.flow.Flow


interface FirebaseTransactionDao {
    fun getTransactions(getTransactionsModel: GetTransactionsModel): Flow<List<TransactionModel>>
    suspend fun saveTransaction(saveTransactionModel: SaveTransactionModel)
    suspend fun deleteTransaction(deleteTransactionModel: DeleteTransactionModel)
}