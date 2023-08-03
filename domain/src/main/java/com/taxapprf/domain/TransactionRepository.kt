package com.taxapprf.domain

import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.year.SaveYearSumModel
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getTransactionModel(transactionKey: String): Flow<TransactionModel>
    fun getTransactions(account: String, year: String): Flow<List<TransactionModel>>
    fun saveTransactionModel(saveTransactionModel: SaveTransactionModel): Flow<Unit>
    fun deleteTransaction(requestModel: FirebaseRequestModel): Flow<Unit>

    fun getYearSum(requestModel: FirebaseRequestModel): Flow<Double>
    fun saveYearSum(saveYearSumModel: SaveYearSumModel): Flow<Unit>
    fun deleteYearSum(requestModel: FirebaseRequestModel): Flow<Unit>
}