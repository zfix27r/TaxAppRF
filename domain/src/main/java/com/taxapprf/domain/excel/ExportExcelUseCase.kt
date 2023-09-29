package com.taxapprf.domain.excel

import com.taxapprf.domain.ExcelRepository
import com.taxapprf.domain.ReportRepository
import com.taxapprf.domain.TransactionRepository
import javax.inject.Inject

class ExportExcelUseCase @Inject constructor(
    private val reportRepository: ReportRepository,
    private val transactionRepository: TransactionRepository,
    private val excelRepository: ExcelRepository
) {
    suspend fun execute(exportExcelModel: ExportExcelModel) =
        with(exportExcelModel) {
            reportRepository.getReport(reportId)?.let { report ->
                reportName = report.name
                reportTax = report.tax
                transactions = transactionRepository.inflateExcelTransactions(exportExcelModel)

                excelRepository.export(exportExcelModel)
            }
        }
}