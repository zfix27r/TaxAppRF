package com.taxapprf.data.remote.firebase

import com.taxapprf.data.remote.firebase.dao.RemoteReportDao
import com.taxapprf.data.remote.firebase.model.FirebaseReportModel
import kotlinx.coroutines.tasks.await


class FirebaseReportDaoImpl(
    private val fb: FirebaseAPI,
) : RemoteReportDao {
    override suspend fun getAll(accountKey: String) =
        let {
/*            fb.getReportsPath(accountKey)
                .setValue(null)
                .await()*/

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