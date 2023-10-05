package com.taxapprf.domain.update

import com.taxapprf.domain.CurrencyRepository
import com.taxapprf.domain.ReportRepository
import com.taxapprf.domain.TransactionRepository
import javax.inject.Inject

class UpdateReportWithTransactionTaxUseCase @Inject constructor(
    private val reportRepository: ReportRepository,
    private val transactionRepository: TransactionRepository,
    private val cbrRepository: CurrencyRepository,
) {
    suspend fun execute(updateReportWithTransactionTaxModel: UpdateReportWithTransactionTaxModel) {
        with(updateReportWithTransactionTaxModel) {
            cbrRepository.getCurrencyRate(currencyOrdinal, date)?.let {
                rate = it
                reportRepository.updateTax(updateReportWithTransactionTaxModel)
                transactionRepository.updateTax(updateReportWithTransactionTaxModel)
            }
        }
    }
}