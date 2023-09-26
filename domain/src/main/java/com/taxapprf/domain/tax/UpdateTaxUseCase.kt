package com.taxapprf.domain.tax

import com.taxapprf.domain.CBRRepository
import com.taxapprf.domain.ReportRepository
import com.taxapprf.domain.TransactionRepository
import javax.inject.Inject

class UpdateTaxUseCase @Inject constructor(
    private val reportRepository: ReportRepository,
    private val transactionRepository: TransactionRepository,
    private val cbrRepository: CBRRepository,
) {
    suspend fun execute(updateTaxModel: UpdateTaxModel) {
        with(updateTaxModel) {
            cbrRepository.getCurrencyRate(currencyId, date)?.let {
                rate = it
                reportRepository.updateTax(updateTaxModel)
                transactionRepository.updateTax(updateTaxModel)
            }
        }
    }
}