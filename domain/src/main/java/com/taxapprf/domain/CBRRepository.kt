package com.taxapprf.domain

import com.taxapprf.domain.cbr.CurrencyModel
import com.taxapprf.domain.cbr.RateWithCurrencyModel

interface CBRRepository {
    suspend fun getCurrencyRate(currencyRateId: Int, date: Long): Double?
    suspend fun getCurrencies(): List<CurrencyModel>
    suspend fun getCurrenciesWithRate(date: Long): List<RateWithCurrencyModel>
}