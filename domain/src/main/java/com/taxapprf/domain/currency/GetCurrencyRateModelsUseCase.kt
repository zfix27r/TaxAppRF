package com.taxapprf.domain.currency

import com.taxapprf.domain.CurrencyRepository
import javax.inject.Inject

class GetCurrencyRateModelsUseCase @Inject constructor(
    private val currencyRepository: CurrencyRepository
) {
    suspend fun execute(date: Long) =
        currencyRepository.getCurrencyRateModels(date)
}