package com.taxapprf.domain

import com.taxapprf.domain.transaction.DeleteTransactionModel
import com.taxapprf.domain.transaction.GetTransactionModel
import com.taxapprf.domain.transaction.GetTransactionsModel
import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.transaction.TransactionsModel
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getTransaction(getTransactionModel: GetTransactionModel): Flow<TransactionModel>
    fun getTransactions(getTransactionsModel: GetTransactionsModel): Flow<TransactionsModel?>
    fun saveTransactionModel(saveTransactionModel: SaveTransactionModel): Flow<Unit>
    fun deleteTransaction(deleteTransactionModel: DeleteTransactionModel): Flow<Unit>
}