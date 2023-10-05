package com.taxapprf.domain

import com.taxapprf.domain.cbr.CurrencyRateModel

interface CurrencyRepository {
    suspend fun getCurrencyRate(currencyOrdinal: Int, date: Long): Double?
    suspend fun getCurrencyRateModels(date: Long): List<CurrencyRateModel>
}