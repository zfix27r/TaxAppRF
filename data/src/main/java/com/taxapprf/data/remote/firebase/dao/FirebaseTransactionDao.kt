package com.taxapprf.data.remote.firebase.dao

import com.taxapprf.domain.FirebasePathModel
import com.taxapprf.data.remote.firebase.model.FirebaseTransactionModel
import com.taxapprf.domain.transaction.TransactionModel
import kotlinx.coroutines.flow.Flow


interface FirebaseTransactionDao {
    fun getTransactions(firebasePathModel: FirebasePathModel): Flow<List<TransactionModel>>
    suspend fun saveTransaction(
        firebasePathModel: FirebasePathModel,
        firebaseTransactionModel: FirebaseTransactionModel
    )

    suspend fun deleteTransaction(firebasePathModel: FirebasePathModel)
}