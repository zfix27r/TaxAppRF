package com.taxapprf.domain.cbr

import com.taxapprf.domain.CurrencyRepository
import javax.inject.Inject

class GetCurrencyRateModelsUseCase @Inject constructor(
    private val currencyRepository: CurrencyRepository
) {
    suspend fun execute(date: Long) =
        currencyRepository.getCurrencyRateModels(date)
}