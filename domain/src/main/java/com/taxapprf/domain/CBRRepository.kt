package com.taxapprf.domain

import com.taxapprf.domain.cbr.CurrencyModel
import com.taxapprf.domain.cbr.CurrencyWithRateModel

interface CBRRepository {
    suspend fun getCurrencyRate(currencyId: Int, date: Long): Double?
    suspend fun getCurrencies(): List<CurrencyModel>
    suspend fun getCurrenciesWithRate(date: Long): List<CurrencyWithRateModel>
}