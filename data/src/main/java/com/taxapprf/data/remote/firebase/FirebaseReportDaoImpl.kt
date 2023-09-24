package com.taxapprf.data.remote.firebase

import com.taxapprf.data.remote.firebase.dao.RemoteReportDao
import com.taxapprf.data.remote.firebase.model.FirebaseReportModel
import com.taxapprf.data.safeCall
import kotlinx.coroutines.tasks.await


class FirebaseReportDaoImpl(
    private val fb: FirebaseAPI,
) : RemoteReportDao {
    override suspend fun getKey(accountKey: String): String? =
        safeCall {
            fb.getReportsPath(accountKey)
                .push()
                .key
        }

    override suspend fun getAll(accountKey: String) =
        fb.getReportsPath(accountKey)
            .get()
            .await()
            .children
            .mapNotNull {
                it.getValue(FirebaseReportModel::class.java)
                    ?.apply {
                        key = it.key
                    }
            }

    override suspend fun updateAll(
        accountKey: String,
        reportModels: Map<String, FirebaseReportModel?>
    ) {
        fb.getReportsPath(accountKey)
            .updateChildren(reportModels)
            .await()
    }
}