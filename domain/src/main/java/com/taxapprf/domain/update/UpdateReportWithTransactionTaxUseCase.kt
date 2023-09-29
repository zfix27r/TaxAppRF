package com.taxapprf.domain.update

import com.taxapprf.domain.CBRRepository
import com.taxapprf.domain.ReportRepository
import com.taxapprf.domain.TransactionRepository
import javax.inject.Inject

class UpdateReportWithTransactionTaxUseCase @Inject constructor(
    private val reportRepository: ReportRepository,
    private val transactionRepository: TransactionRepository,
    private val cbrRepository: CBRRepository,
) {
    suspend fun execute(updateReportWithTransactionTaxModel: UpdateReportWithTransactionTaxModel) {
        with(updateReportWithTransactionTaxModel) {
            cbrRepository.getCurrencyRate(currencyId, date)?.let {
                rate = it
                println(updateReportWithTransactionTaxModel)
                println(tax)
                reportRepository.updateTax(updateReportWithTransactionTaxModel)
                transactionRepository.updateTax(updateReportWithTransactionTaxModel)
            }
        }
    }
}