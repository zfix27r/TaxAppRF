package com.taxapprf.domain.transaction

import com.taxapprf.domain.ReportRepository
import com.taxapprf.domain.TransactionRepository
import javax.inject.Inject

class SaveTransactionUseCase @Inject constructor(
    private val reportRepository: ReportRepository,
    private val transactionRepository: TransactionRepository,
) {
    suspend fun execute(saveTransactionModel: SaveTransactionModel) {
        transactionRepository.save(saveTransactionModel)
        reportRepository.updateAfterUpdateTransaction(saveTransactionModel)
    }
}