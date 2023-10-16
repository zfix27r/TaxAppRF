package com.taxapprf.data.remote.firebase

import com.taxapprf.data.remote.firebase.dao.RemoteTransactionDao
import com.taxapprf.data.remote.firebase.entity.FirebaseTransactionEntity
import com.taxapprf.data.safeCall
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class FirebaseTransactionDaoImpl @Inject constructor(
    private val fb: Firebase,
) : RemoteTransactionDao {
    override suspend fun getKey(accountKey: String, reportKey: String) =
        safeCall {
            fb.getTransactionsPath(accountKey, reportKey).push().key
        }

    override suspend fun getAll(accountKey: String, reportKey: String) =
        safeCall {
            fb.getTransactionsPath(accountKey, reportKey)
                .get()
                .await()
                .children.mapNotNull {
                    it.getValue(FirebaseTransactionEntity::class.java)
                        ?.apply {
                            key = it.key
                        }
                }
        }

    override suspend fun deleteAllTransactions(
        accountKey: String,
        reportKey: String
    ) {
        safeCall {
            fb.getTransactionsPath(accountKey, reportKey)
                .setValue(null)
                .await()
        }
    }

    override suspend fun updateTransactions(
        accountKey: String,
        reportKey: String,
        transactionsModels: Map<String, FirebaseTransactionEntity?>
    ) {
        safeCall {
            fb.getTransactionsPath(accountKey, reportKey)
                .updateChildren(transactionsModels)
                .await()
        }
    }
}