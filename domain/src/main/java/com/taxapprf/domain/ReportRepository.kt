package com.taxapprf.domain

import com.taxapprf.domain.report.DeleteReportModel
import com.taxapprf.domain.report.ReportAdapterModel
import kotlinx.coroutines.flow.Flow

interface ReportRepository {
    fun getReport(accountKey: String): Flow<List<ReportAdapterModel>>
    fun deleteReport(deleteReportModel: DeleteReportModel): Flow<Unit>
    fun saveReportFromExcel(storagePath: String): Flow<Unit>
}