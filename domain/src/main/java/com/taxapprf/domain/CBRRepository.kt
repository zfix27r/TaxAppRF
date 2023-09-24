package com.taxapprf.domain

import com.taxapprf.domain.currency.CurrencyWithRateModel

interface CBRRepository {
    suspend fun getRate(date: Long, currencyCharCode: String): Double?
    suspend fun getCurrenciesWithRate(date: Long): List<CurrencyWithRateModel>
}