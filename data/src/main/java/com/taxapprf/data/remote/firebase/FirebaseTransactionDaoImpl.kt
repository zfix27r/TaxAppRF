package com.taxapprf.data.remote.firebase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.taxapprf.data.error.DataErrorPassKeyIsEmpty
import com.taxapprf.data.remote.firebase.dao.FirebaseTransactionDao
import com.taxapprf.data.remote.firebase.model.FirebaseTransactionModel
import com.taxapprf.data.safeCall
import com.taxapprf.domain.FirebasePathModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class FirebaseTransactionDaoImpl @Inject constructor(
    private val fb: FirebaseAPI,
) : FirebaseTransactionDao {
    override fun getTransactions(firebasePathModel: FirebasePathModel) =
        callbackFlow {
            safeCall {
                val reference = fb.getTransactionsPath(firebasePathModel)

                val callback = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val reports = snapshot.children.mapNotNull {
                            it.getValue(FirebaseTransactionModel::class.java)
                                ?.toTransactionModel(it.key)
                        }

                        trySendBlocking(reports)
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                }

                reference.addValueEventListener(callback)

                awaitClose { reference.removeEventListener(callback) }
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
                .child(firebasePathModel.transactionKey ?: throw DataErrorPassKeyIsEmpty())
                .setValue(null)
                .await()
        }
    }
}