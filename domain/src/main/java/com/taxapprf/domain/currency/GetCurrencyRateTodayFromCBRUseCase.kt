package com.taxapprf.domain.currency

import com.taxapprf.domain.CurrencyRepository
import javax.inject.Inject

class GetCurrencyRateTodayFromCBRUseCase @Inject constructor(
    private val repository: CurrencyRepository
) {
    fun execute(currency: String) = repository.getTodayRate(currency)
}