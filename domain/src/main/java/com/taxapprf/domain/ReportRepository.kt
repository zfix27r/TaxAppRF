package com.taxapprf.domain

import android.net.Uri
import com.taxapprf.domain.report.DeleteReportModel
import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.transaction.TransactionModel
import kotlinx.coroutines.flow.Flow

interface ReportRepository {
    fun getReports(accountKey: String): Flow<List<ReportModel>>
    fun deleteReport(deleteReportModel: DeleteReportModel): Flow<Unit>
    fun saveReportFromExcel(storagePath: String): Flow<Unit>
    fun getExcelReport(report: ReportModel, transactions: List<TransactionModel>): Flow<Uri>
}