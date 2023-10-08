package com.taxapprf.data.remote.firebase

import com.taxapprf.data.remote.firebase.dao.RemoteTransactionDao
import com.taxapprf.data.remote.firebase.model.FirebaseTransactionModel
import com.taxapprf.data.safeCall
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class FirebaseTransactionDaoImpl @Inject constructor(
    private val fb: FirebaseAPI,
) : RemoteTransactionDao {
    override suspend fun getKey(accountKey: String, reportKey: String) =
        fb.getTransactionsPath(accountKey, reportKey).push().key

    override suspend fun getAll(accountKey: String, reportKey: String) =
        safeCall {
/*            fb.getTransactionsPath(accountKey, reportKey)
                .setValue(null)
                .await()*/

            fb.getTransactionsPath(accountKey, reportKey)
                .get()
                .await()
                .children.mapNotNull {
                    it.getValue(FirebaseTransactionModel::class.java)
                        ?.apply {
                            key = it.key
                        }
                }
        }

    override suspend fun updateAll(
        accountKey: String,
        reportKey: String,
        transactionsModels: Map<String, FirebaseTransactionModel?>
    ) {
        safeCall {
            fb.getTransactionsPath(accountKey, reportKey)
                .updateChildren(transactionsModels)
                .await()
        }
    }
}