package com.taxapprf.domain.main

import com.taxapprf.domain.CurrencyRepository
import com.taxapprf.domain.MainRepository
import javax.inject.Inject

class SaveTransaction1UseCase @Inject constructor(
    private val mainRepository: MainRepository,
    private val currencyRepository: CurrencyRepository,
) {
    suspend fun execute(saveTransaction1Model: SaveTransaction1Model) {
        saveTransaction1Model.rate = currencyRepository.getCurrencyRate(
            saveTransaction1Model.currencyOrdinal,
            saveTransaction1Model.date
        )

        mainRepository.saveTransaction(saveTransaction1Model)
    }
}