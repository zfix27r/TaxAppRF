package com.taxapprf.domain.transaction

import com.taxapprf.domain.CurrencyRepository
import com.taxapprf.domain.NetworkManager
import com.taxapprf.domain.ReportRepository
import com.taxapprf.domain.TransactionRepository
import javax.inject.Inject

class UpdateTaxTransactionUseCase @Inject constructor(
    private val networkManager: NetworkManager,
    private val reportRepository: ReportRepository,
    private val transactionRepository: TransactionRepository,
    private val currencyRepository: CurrencyRepository,
) {
    suspend fun execute(saveTransactionModel: SaveTransactionModel) {
        if (networkManager.available) {
            with(saveTransactionModel) {
                val newRateCBRF = currencyRepository.getCurrencyRate(date, currency)
                if (newRateCBRF > 0) {
                    rateCBRF = newRateCBRF
                    transactionRepository.save(saveTransactionModel)
                    reportRepository.updateAfterUpdateTransaction(saveTransactionModel)
                }
            }
        }
    }
}