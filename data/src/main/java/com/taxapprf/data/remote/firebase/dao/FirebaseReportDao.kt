package com.taxapprf.data.remote.firebase.dao

import com.taxapprf.data.remote.firebase.model.FirebaseReportModel
import com.taxapprf.domain.report.DeleteReportModel
import com.taxapprf.domain.report.SaveReportModel


interface FirebaseReportDao {
    suspend fun getReports(accountKey: String): List<FirebaseReportModel>
    suspend fun addReport(saveReportModel: SaveReportModel)
    suspend fun updateReport(saveReportModel: SaveReportModel)
    suspend fun deleteReport(deleteReportModel: DeleteReportModel)
}