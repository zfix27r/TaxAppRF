package com.taxapprf.data.local.excel

import android.net.Uri
import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.transaction.TransactionModel
import kotlinx.coroutines.flow.Flow

interface ExcelDao {
    fun sendReport(report: ReportModel, transactions: List<TransactionModel>): Flow<Uri>
}