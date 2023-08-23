package com.taxapprf.domain

import com.taxapprf.domain.report.DeleteReportModel
import com.taxapprf.domain.report.GetReportsModel
import com.taxapprf.domain.report.ReportModel
import kotlinx.coroutines.flow.Flow

interface ReportRepository {
    fun getReports(getReportsModel: GetReportsModel): Flow<Result<List<ReportModel>>>
    fun deleteReport(deleteReportModel: DeleteReportModel): Flow<Unit>
}