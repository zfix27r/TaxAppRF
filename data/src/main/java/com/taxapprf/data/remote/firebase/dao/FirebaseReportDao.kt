package com.taxapprf.data.remote.firebase.dao

import com.taxapprf.domain.report.DeleteReportModel
import com.taxapprf.domain.report.ObserveReportsModel
import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.report.SaveReportModel
import kotlinx.coroutines.flow.Flow

interface FirebaseReportDao {
    fun getReports(getReportsModel: ObserveReportsModel): Flow<Result<List<ReportModel>>>
    suspend fun getReport(getReportModel: GetReportModel): ReportModel
    suspend fun saveReport(saveReportModel: SaveReportModel)
    suspend fun deleteReport(deleteReportModel: DeleteReportModel)
}