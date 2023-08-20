package com.taxapprf.domain

import com.taxapprf.domain.transaction.DeleteTransactionModel
import com.taxapprf.domain.transaction.GetTransactionsModel
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.transaction.TransactionModel
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getTransactions(getTransactionModel: GetTransactionsModel): Flow<List<TransactionModel>>
    fun saveTransactionModel(saveTransactionModel: SaveTransactionModel): Flow<Unit>
    fun deleteTransaction(deleteTransactionModel: DeleteTransactionModel): Flow<Unit>
}