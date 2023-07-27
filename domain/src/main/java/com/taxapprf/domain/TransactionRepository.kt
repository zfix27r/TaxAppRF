package com.taxapprf.domain

import com.taxapprf.domain.transaction.GetTransactionModel
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.year.SaveYearSumModel
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getTransactionModel(requestModel: FirebaseRequestModel): Flow<GetTransactionModel>
    fun getTransactionModels(requestModel: FirebaseRequestModel): Flow<List<GetTransactionModel>>
    fun saveTransactionModel(saveTransactionModel: SaveTransactionModel): Flow<Unit>
    fun deleteTransaction(requestModel: FirebaseRequestModel): Flow<Unit>

    fun getYearSum(requestModel: FirebaseRequestModel): Flow<Double>
    fun saveYearSum(saveYearSumModel: SaveYearSumModel): Flow<Unit>
    fun deleteYearSum(requestModel: FirebaseRequestModel): Flow<Unit>
}