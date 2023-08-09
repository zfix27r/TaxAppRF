package com.taxapprf.domain

import com.taxapprf.domain.report.ReportModel
import kotlinx.coroutines.flow.Flow

interface ReportRepository {
    fun getReports(accountName: String): Flow<List<ReportModel>>
    fun deleteReport(firebasePathModel: FirebasePathModel): Flow<Unit>
    fun saveReportFromExcel(storagePath: String): Flow<Unit>
}