package com.taxapprf.domain

import com.taxapprf.domain.transaction.DeleteTransactionModel
import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.transaction.TransactionsModel
import com.taxapprf.domain.year.SaveYearSumModel
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getTransaction(transactionKey: String): Flow<TransactionModel>
    fun getTransactions(account: String, year: String): Flow<TransactionsModel?>
    fun saveTransactionModel(transaction: SaveTransactionModel): Flow<Unit>
    fun deleteTransaction(deleteTransactionModel: DeleteTransactionModel): Flow<Unit>
    fun deleteTransactions(deleteTransactionModel: DeleteTransactionModel): Flow<Unit>
    fun getYearSum(requestModel: FirebaseRequestModel): Flow<Double>
    fun saveYearSum(saveYearSumModel: SaveYearSumModel): Flow<Unit>
    fun deleteYearSum(requestModel: FirebaseRequestModel): Flow<Unit>
}