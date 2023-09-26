package com.taxapprf.domain.transaction

import com.taxapprf.domain.ReportRepository
import com.taxapprf.domain.TransactionRepository
import com.taxapprf.domain.tax.UpdateTaxModel
import com.taxapprf.domain.tax.UpdateTaxUseCase
import javax.inject.Inject

class SaveTransactionUseCase @Inject constructor(
    private val reportRepository: ReportRepository,
    private val transactionRepository: TransactionRepository,
    private val updateTaxUseCase: UpdateTaxUseCase
) {
    suspend fun execute(saveTransactionModel: SaveTransactionModel) {
        with(saveTransactionModel) {
            newReportId = reportRepository.getReportId(accountId, reportId, date, tax)
            newTransactionId = transactionRepository.save(saveTransactionModel)?.toInt()
        }

        saveTransactionModel.toUpdateTaxModel()?.let {
            updateTaxUseCase.execute(it)
        }
    }

    private fun SaveTransactionModel.toUpdateTaxModel(): UpdateTaxModel? {
        val reportId = if (transactionId == null) newReportId else null
        val transactionId = newTransactionId ?: return null

        return UpdateTaxModel(
            reportId = reportId,
            transactionId = transactionId,
            type = type,
            currencyId = currencyId,
            date = date,
            sum = sum,
            oldTax = tax
        )
    }
}