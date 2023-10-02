package com.taxapprf.domain.delete

import com.taxapprf.domain.ReportRepository
import com.taxapprf.domain.TransactionRepository
import javax.inject.Inject

class DeleteReportWithTransactionsUseCase @Inject constructor(
    private val reportRepository: ReportRepository,
    private val transactionRepository: TransactionRepository,
) {
    suspend fun execute(deleteReportWithTransactionsModel: DeleteReportWithTransactionsModel) {
        reportRepository.deleteReport(deleteReportWithTransactionsModel)
        transactionRepository.deleteReportTransactions(deleteReportWithTransactionsModel)
    }
}