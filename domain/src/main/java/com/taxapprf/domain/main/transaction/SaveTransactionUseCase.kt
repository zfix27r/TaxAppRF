package com.taxapprf.domain.main.transaction

import com.taxapprf.domain.CurrencyRepository
import com.taxapprf.domain.MainRepository
import javax.inject.Inject

class SaveTransactionUseCase @Inject constructor(
    private val mainRepository: MainRepository,
    private val currencyRepository: CurrencyRepository,
) {
    suspend fun execute(saveTransactionModel: SaveTransactionModel) {
        saveTransactionModel.rate = currencyRepository.getCurrencyRate(
            saveTransactionModel.currencyOrdinal,
            saveTransactionModel.date
        )

        mainRepository.saveTransaction(saveTransactionModel)
    }
}