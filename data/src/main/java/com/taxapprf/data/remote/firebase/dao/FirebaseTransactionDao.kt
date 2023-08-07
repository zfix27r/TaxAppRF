package com.taxapprf.data.remote.firebase.dao

import com.taxapprf.domain.FirebasePathModel
import com.taxapprf.data.remote.firebase.model.FirebaseTransactionModel


interface FirebaseTransactionDao {
    suspend fun getTransactions(firebasePathModel: FirebasePathModel): List<FirebaseTransactionModel>
    suspend fun getTransaction(firebasePathModel: FirebasePathModel): FirebaseTransactionModel?
    suspend fun saveTransaction(
        firebasePathModel: FirebasePathModel,
        firebaseTransactionModel: FirebaseTransactionModel
    )

    suspend fun deleteTransaction(firebasePathModel: FirebasePathModel)
}