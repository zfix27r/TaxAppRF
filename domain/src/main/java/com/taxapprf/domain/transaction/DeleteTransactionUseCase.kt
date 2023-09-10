package com.taxapprf.domain.transaction

import com.taxapprf.domain.ReportRepository
import com.taxapprf.domain.TransactionRepository
import javax.inject.Inject

class DeleteTransactionUseCase @Inject constructor(
    private val reportRepository: ReportRepository,
    private val transactionRepository: TransactionRepository
) {
    suspend fun execute(deleteTransactionModel: DeleteTransactionModel) {
        transactionRepository.delete(deleteTransactionModel)
        reportRepository.updateAfterUpdateTransaction(deleteTransactionModel)
    }
}