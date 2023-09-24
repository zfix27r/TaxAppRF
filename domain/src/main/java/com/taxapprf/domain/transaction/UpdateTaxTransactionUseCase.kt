package com.taxapprf.domain.transaction

import com.taxapprf.domain.CBRRepository
import com.taxapprf.domain.NetworkManager
import com.taxapprf.domain.ReportRepository
import com.taxapprf.domain.TransactionRepository
import javax.inject.Inject

class UpdateTaxTransactionUseCase @Inject constructor(
    private val networkManager: NetworkManager,
    private val reportRepository: ReportRepository,
    private val transactionRepository: TransactionRepository,
    private val currencyRepository: CBRRepository,
) {
    suspend fun execute(saveTransactionModel: SaveTransactionModel) {
        if (networkManager.available) {
            with(saveTransactionModel) {
                currencyRepository.getRate(date, currencyCharCode)?.let { rate ->
                    rateCBRF = rate
                    transactionRepository.save(saveTransactionModel)
                    reportRepository.updateAfterUpdateTransaction(saveTransactionModel)
                }
            }
        }
    }
}