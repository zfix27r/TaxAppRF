package com.taxapprf.data.remote.firebase

import com.taxapprf.data.remote.firebase.dao.RemoteReportDao
import com.taxapprf.data.remote.firebase.entity.FirebaseReportEntity
import kotlinx.coroutines.tasks.await


class FirebaseReportDaoImpl(
    private val fb: Firebase,
) : RemoteReportDao {
    override suspend fun getAll(accountKey: String) =
        let {
            fb.getReportsPath(accountKey)
                .get()
                .await()
                .children
                .mapNotNull {
                    it.getValue(FirebaseReportEntity::class.java)
                        ?.apply {
                            key = it.key
                        }
                }
        }

    override suspend fun updateAll(
        accountKey: String,
        reportModels: Map<String, FirebaseReportEntity?>
    ) {
        fb.getReportsPath(accountKey)
            .updateChildren(reportModels)
            .await()
    }
}