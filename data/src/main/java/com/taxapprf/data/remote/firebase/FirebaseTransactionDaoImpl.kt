package com.taxapprf.data.remote.firebase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.taxapprf.data.error.external.DataErrorExternalEmpty
import com.taxapprf.data.error.internal.DataErrorInternalAccountKeyEmpty
import com.taxapprf.data.error.internal.DataErrorInternalYearKeyEmpty
import com.taxapprf.data.remote.firebase.dao.FirebaseTransactionDao
import com.taxapprf.data.remote.firebase.model.FirebaseTransactionModel
import com.taxapprf.data.safeCall
import com.taxapprf.domain.transaction.DeleteTransactionModel
import com.taxapprf.domain.transaction.GetTransactionsModel
import com.taxapprf.domain.transaction.SaveTransactionModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class FirebaseTransactionDaoImpl @Inject constructor(
    private val fb: FirebaseAPI,
) : FirebaseTransactionDao {
    override fun getTransactions(getTransactionsModel: GetTransactionsModel) = callbackFlow {
        safeCall {
            val reference =
                fb.getTransactionsPath(getTransactionsModel.accountName, getTransactionsModel.year)

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

            awaitClose {
                reference.removeEventListener(callback)
            }
        }
    }

    override suspend fun saveTransaction(saveTransactionModel: SaveTransactionModel) {
        safeCall {
            with(saveTransactionModel) {
                val accountKey = accountKey ?: throw DataErrorInternalAccountKeyEmpty()
                val yearKey = yearKey ?: throw DataErrorInternalYearKeyEmpty()

                val key = transactionKey
                    ?: fb.getTransactionsPath(accountKey, yearKey).push().key
                    ?: throw DataErrorExternalEmpty()

                fb.getTransactionsPath(accountKey, yearKey)
                    .child(key)
                    .setValue(saveTransactionModel.toFirebaseTransactionModel())
                    .await()
            }
        }
    }

    override suspend fun deleteTransaction(deleteTransactionModel: DeleteTransactionModel) {
        safeCall {
            with(deleteTransactionModel) {
                fb.getTransactionsPath(accountKey, yearKey)
                    .child(transactionKey)
                    .setValue(null)
                    .await()
            }
        }
    }

    private fun SaveTransactionModel.toFirebaseTransactionModel() =
        FirebaseTransactionModel(name, date, type, currency, rateCBR, sum, tax)
}