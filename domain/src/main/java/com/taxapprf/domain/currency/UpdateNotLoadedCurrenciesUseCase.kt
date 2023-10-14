package com.taxapprf.domain.currency

import com.taxapprf.domain.CurrencyRepository
import javax.inject.Inject

class UpdateNotLoadedCurrenciesUseCase @Inject constructor(
    private val currencyRepository: CurrencyRepository
) {
    suspend fun execute() =
        currencyRepository.updateNotLoadedCurrencies()
}