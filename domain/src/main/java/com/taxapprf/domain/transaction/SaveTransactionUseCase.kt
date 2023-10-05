package com.taxapprf.domain.transaction

import com.taxapprf.domain.ReportRepository
import com.taxapprf.domain.TransactionRepository
import com.taxapprf.domain.update.UpdateReportWithTransactionTaxModel
import com.taxapprf.domain.update.UpdateReportWithTransactionTaxUseCase
import javax.inject.Inject

class SaveTransactionUseCase @Inject constructor(
    private val reportRepository: ReportRepository,
    private val transactionRepository: TransactionRepository,
    private val updateReportWithTransactionTaxUseCase: UpdateReportWithTransactionTaxUseCase
) {
    suspend fun execute(saveTransactionModel: SaveTransactionModel) {
        with(saveTransactionModel) {
            newReportId =
                reportRepository.updateWithCUDTransaction(
                    accountId,
                    reportId,
                    transactionId,
                    date,
                    tax
                )
            newTransactionId = transactionRepository.save(saveTransactionModel)?.toInt()
        }

        saveTransactionModel.toUpdateTaxModel()?.let {
            updateReportWithTransactionTaxUseCase.execute(it)
        }
    }

    private fun SaveTransactionModel.toUpdateTaxModel(): UpdateReportWithTransactionTaxModel? {
        val reportId =
            if (transactionId == null || reportId != newReportId) newReportId else reportId
        val transactionId = newTransactionId ?: return null

        return UpdateReportWithTransactionTaxModel(
            reportId = reportId,
            transactionId = transactionId,
            typeK = type.k,
            currencyOrdinal = currencyOrdinal,
            date = date,
            sum = sum,
            oldTax = tax
        )
    }
}