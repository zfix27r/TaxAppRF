package com.taxapprf.domain.delete

import com.taxapprf.domain.ReportRepository
import com.taxapprf.domain.TransactionRepository
import javax.inject.Inject

class DeleteTransactionWithReportUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val reportRepository: ReportRepository,
) {
    suspend fun execute(deleteTransactionWithReportModel: DeleteTransactionWithReportModel) {
        transactionRepository.deleteTransaction(deleteTransactionWithReportModel)
            ?.let { updatedDeleteTransactionWithReportModel ->
                reportRepository.deleteOrUpdateReport(updatedDeleteTransactionWithReportModel)
            }
    }
}