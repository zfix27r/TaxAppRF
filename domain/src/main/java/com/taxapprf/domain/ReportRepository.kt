package com.taxapprf.domain

import com.taxapprf.domain.report.DeleteReportModel
import com.taxapprf.domain.report.ObserveReportsModel
import com.taxapprf.domain.report.ObserveReportModel
import com.taxapprf.domain.report.ReportModel
import kotlinx.coroutines.flow.Flow

interface ReportRepository {
    fun observeReport(observeReportModel: ObserveReportModel): Flow<List<ReportModel>>
    fun observeReports(observeReportsModel: ObserveReportsModel): Flow<List<ReportModel>>
    fun deleteReport(deleteReportModel: DeleteReportModel): Flow<Unit>
}