package com.taxapprf.data.remote.firebase

import com.taxapprf.data.error.DataErrorPassKeyIsEmpty
import com.taxapprf.data.remote.firebase.dao.FirebaseTransactionDao
import com.taxapprf.domain.FirebasePathModel
import com.taxapprf.data.remote.firebase.model.FirebaseTransactionModel
import com.taxapprf.data.safeCall
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class FirebaseTransactionDaoImpl @Inject constructor(
    private val fb: FirebaseAPI,
) : FirebaseTransactionDao {
    override suspend fun getTransactions(firebasePathModel: FirebasePathModel) =
        safeCall {
            fb.getTransactionsPath(firebasePathModel)
                .get()
                .await()
                .children
                .mapNotNull {
                    it.getValue(FirebaseTransactionModel::class.java)?.toTransactionModel(it.key)
                }
        }

    /*    override suspend fun getTransaction(firebasePathModel: FirebasePathModel): FirebaseTransactionModel? =
            safeCall {
                val dataSnapshot = fb.getTransactionsPath(firebasePathModel)
                    .child(firebasePathModel.transactionKey!!)
                    .get()
                    .await()

                val firebaseTransactionModel =
                    dataSnapshot.getValue(FirebaseTransactionModel::class.java)
                firebaseTransactionModel.toTransactionModel(firebaseTransactionModel.k)
            }*/

    override suspend fun saveTransaction(
        firebasePathModel: FirebasePathModel,
        firebaseTransactionModel: FirebaseTransactionModel
    ) {
        safeCall {
            with(firebaseTransactionModel) {
                val key = firebasePathModel.transactionKey
                    ?: fb.getTransactionsPath(firebasePathModel).push().key
                    ?: throw DataErrorPassKeyIsEmpty()

                fb.getTransactionsPath(firebasePathModel)
                    .child(key)
                    .setValue(firebaseTransactionModel)
                    .await()
            }
        }
    }

    override suspend fun deleteTransaction(firebasePathModel: FirebasePathModel) {
        safeCall {
            fb.getTransactionsPath(firebasePathModel)
                .child(firebasePathModel.transactionKey!!)
                .setValue(null)
                .await()
        }
    }
}