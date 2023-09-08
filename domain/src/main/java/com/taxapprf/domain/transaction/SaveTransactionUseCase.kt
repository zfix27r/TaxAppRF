package com.taxapprf.domain.transaction

import com.taxapprf.domain.CurrencyRepository
import com.taxapprf.domain.NetworkManager
import com.taxapprf.domain.ReportRepository
import com.taxapprf.domain.TransactionRepository
import javax.inject.Inject

class SaveTransactionUseCase @Inject constructor(
    private val networkManager: NetworkManager,
    private val reportRepository: ReportRepository,
    private val transactionRepository: TransactionRepository,
    private val currencyRepository: CurrencyRepository,
) {
    suspend fun execute(saveTransactionModel: SaveTransactionModel) {
        transactionRepository.save(saveTransactionModel)
        reportRepository.updateAfterSaveTransaction(saveTransactionModel)

/*        if (networkManager.available) {
            with(saveTransactionModel) {
                if (tax == null) {
                    val rateCBRF = currencyRepository.getCurrencyRate(date, currency)
                    transactionRepository.update(saveTransactionModel, rateCBRF)
                }
            }
        }*/
    }
}