package com.taxapprf.data.remote.firebase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.taxapprf.data.error.DataErrorExternal
import com.taxapprf.data.remote.firebase.dao.RemoteTransactionDao
import com.taxapprf.data.remote.firebase.model.FirebaseTransactionModel
import com.taxapprf.data.safeCall
import com.taxapprf.domain.transaction.TransactionModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class FirebaseTransactionDaoImpl @Inject constructor(
    private val fb: FirebaseAPI,
) : RemoteTransactionDao {
    override fun observeAll(
        accountKey: String,
        reportKey: String
    ) =
        callbackFlow<Result<List<TransactionModel>>> {
            safeCall {
                val reference = fb.getTransactionsPath(accountKey, reportKey)

                val callback = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val transactions = snapshot.children.mapNotNull {
                            it.getValue(FirebaseTransactionModel::class.java)
                                ?.toTransactionModel(it.key)
                        }

                        trySendBlocking(Result.success(transactions))
                    }

                    override fun onCancelled(error: DatabaseError) {
                        trySendBlocking(Result.failure(DataErrorExternal(error.message)))
                    }
                }

                reference.addValueEventListener(callback)

                awaitClose {
                    reference.removeEventListener(callback)
                }
            }
        }

    override suspend fun saveAll(
        accountKey: String,
        reportKey: String,
        mapFirebaseTransactionsModel: Map<String, FirebaseTransactionModel>
    ) {
        safeCall {
            fb.getTransactionsPath(accountKey, reportKey)
                .updateChildren(mapFirebaseTransactionsModel)
                .await()
        }
    }

    override suspend fun save(
        accountKey: String,
        reportKey: String,
        transactionKey: String?,
        firebaseTransactionModel: FirebaseTransactionModel
    ) {
        safeCall {
            val ref = fb.getTransactionsPath(accountKey, reportKey)

            val key = transactionKey
                ?: ref.push().key
                ?: throw DataErrorExternal()

            ref
                .child(key)
                .setValue(firebaseTransactionModel)
                .await()
        }
    }

    override suspend fun delete(
        accountKey: String,
        reportKey: String,
        transactionKey: String
    ) {
        safeCall {
            fb.getTransactionsPath(accountKey, reportKey)
                .child(transactionKey)
                .setValue(null)
                .await()
        }
    }

    override suspend fun deleteAll(
        accountKey: String,
        reportKey: String,
        mapFirebaseTransactionsModel: Map<String, FirebaseTransactionModel?>
    ) {
        safeCall {
            fb.getTransactionsPath(accountKey, reportKey)
                .updateChildren(mapFirebaseTransactionsModel)
                .await()
        }
    }
}