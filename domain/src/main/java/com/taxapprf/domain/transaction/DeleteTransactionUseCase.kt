package com.taxapprf.domain.transaction

import com.taxapprf.domain.ReportRepository
import com.taxapprf.domain.TransactionRepository
import javax.inject.Inject

class DeleteTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val reportRepository: ReportRepository,
) {
    suspend fun execute(deleteTransactionModel: DeleteTransactionModel) {
        val step1result = transactionRepository.deleteTransactionStep1(deleteTransactionModel)
        step1result?.let { reportRepository.delete(it) }
    }
}