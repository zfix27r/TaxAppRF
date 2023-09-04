package com.taxapprf.data.remote.firebase.dao

import com.taxapprf.data.remote.firebase.model.FirebaseReportModel
import com.taxapprf.data.remote.firebase.model.GetReportModel
import com.taxapprf.domain.report.DeleteReportModel
import com.taxapprf.domain.report.ObserveReportModel
import com.taxapprf.domain.report.ObserveReportsModel
import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.report.SaveReportModel
import kotlinx.coroutines.flow.Flow

interface FirebaseReportDao {
    fun observeReport(observeReportModel: ObserveReportModel): Flow<Result<ReportModel>>
    fun observeReports(observeReportsModel: ObserveReportsModel): Flow<Result<List<ReportModel>>>
    suspend fun getReport(getReportModel: GetReportModel): ReportModel
    suspend fun saveReport(saveReportModel: SaveReportModel)
    suspend fun saveReports(accountKey: String, reportModels: Map<String, FirebaseReportModel>)
    suspend fun deleteReport(deleteReportModel: DeleteReportModel)
}