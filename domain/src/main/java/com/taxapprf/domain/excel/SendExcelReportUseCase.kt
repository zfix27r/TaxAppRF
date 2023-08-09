package com.taxapprf.domain.excel

import com.taxapprf.domain.ReportRepository
import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.transaction.TransactionModel
import javax.inject.Inject

class SendExcelReportUseCase @Inject constructor(
    private val repository: ReportRepository
) {
    fun execute(report: ReportModel, transactions: List<TransactionModel>) =
        repository.getExcelReport(report, transactions)
}