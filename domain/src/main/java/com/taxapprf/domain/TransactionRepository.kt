package com.taxapprf.domain

import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.transaction.TransactionsModel
import com.taxapprf.domain.year.SaveYearSumModel
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getTransaction(request: FirebaseRequestModel): Flow<TransactionModel>
    fun getTransactions(request: FirebaseRequestModel): Flow<TransactionsModel?>
    fun saveTransactionModel(
        request: FirebaseRequestModel,
        saveTransactionModel: SaveTransactionModel
    ): Flow<Unit>

    fun deleteTransaction(requestModel: FirebaseRequestModel): Flow<Unit>

    fun getYearSum(requestModel: FirebaseRequestModel): Flow<Double>
    fun saveYearSum(saveYearSumModel: SaveYearSumModel): Flow<Unit>
    fun deleteYearSum(requestModel: FirebaseRequestModel): Flow<Unit>
}