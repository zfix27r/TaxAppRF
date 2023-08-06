package com.taxapprf.data.remote.firebase

import com.taxapprf.data.remote.firebase.dao.FirebaseReportDao
import com.taxapprf.data.remote.firebase.model.FirebaseReportModel
import com.taxapprf.data.safeCall
import com.taxapprf.domain.report.DeleteReportModel
import com.taxapprf.domain.report.SaveReportModel
import kotlinx.coroutines.tasks.await


class FirebaseReportDaoImpl(
    private val fb: FirebaseAPI,
) : FirebaseReportDao {
    override suspend fun getReports(accountKey: String): List<FirebaseReportModel> =
        safeCall {
            fb.getReportsPath(accountKey)
                .get()
                .await()
                .children
                .mapNotNull { it.getValue(FirebaseReportModel::class.java) }
        }

    override suspend fun addReport(saveReportModel: SaveReportModel) {
        TODO("Not yet implemented")
    }

    override suspend fun updateReport(saveReportModel: SaveReportModel) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteReport(deleteReportModel: DeleteReportModel) {
        safeCall {
            with(deleteReportModel) {
                fb.getReportPath(accountKey, yearKey)
                    .setValue(null)
                    .await()
            }
        }
    }
}