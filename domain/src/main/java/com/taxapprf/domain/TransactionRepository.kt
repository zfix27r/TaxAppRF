package com.taxapprf.domain

import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.transaction.TransactionModel
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getTransaction(firebasePathModel: FirebasePathModel): Flow<TransactionModel?>
    fun getTransactions(firebasePathModel: FirebasePathModel): Flow<List<TransactionModel>>
    fun saveTransactionModel(saveTransactionModel: SaveTransactionModel): Flow<Unit>
    fun deleteTransaction(firebasePathModel: FirebasePathModel): Flow<Unit>
}