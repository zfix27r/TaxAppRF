package com.taxapprf.data.remote.firebase.dao

import com.taxapprf.data.remote.firebase.model.FirebaseReportModel
import com.taxapprf.domain.report.DeleteReportModel


interface FirebaseReportDao {
    suspend fun getReports(accountKey: String): List<FirebaseReportModel>
    suspend fun addReport(accountKey: String)
    suspend fun deleteReport(deleteReportModel: DeleteReportModel)

}