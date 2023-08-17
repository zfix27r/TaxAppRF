package com.taxapprf.data.remote.firebase.dao

import com.taxapprf.data.remote.firebase.model.FirebaseReportModel
import com.taxapprf.domain.report.DeleteReportModel
import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.report.SaveReportModel
import kotlinx.coroutines.flow.Flow

interface FirebaseReportDao {
    fun getReports(accountKey: String): Flow<Result<List<ReportModel>>>
    suspend fun addReport(saveReportModel: SaveReportModel)
    suspend fun updateReport(saveReportModel: SaveReportModel)
    suspend fun deleteReport(deleteReportModel: DeleteReportModel)
    suspend fun getReportTax(accountKey: String, year: String): FirebaseReportModel?
    suspend fun saveReportTax(
        accountKey: String, year: String, firebaseReportModel: FirebaseReportModel
    )
}