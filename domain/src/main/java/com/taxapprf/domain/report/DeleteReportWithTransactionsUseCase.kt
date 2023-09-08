package com.taxapprf.domain.report

import com.taxapprf.domain.ReportRepository
import com.taxapprf.domain.TransactionRepository
import javax.inject.Inject

class DeleteReportWithTransactionsUseCase @Inject constructor(
    private val reportRepository: ReportRepository,
    private val transactionRepository: TransactionRepository
) {
    suspend fun execute(id: Int, accountKey: String, reportKey: String) {
        reportRepository.delete(id)
        transactionRepository.deleteAll(accountKey, reportKey)
    }
}