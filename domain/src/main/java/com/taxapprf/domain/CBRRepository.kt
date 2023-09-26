package com.taxapprf.domain

import com.taxapprf.domain.cbr.CurrencyModel
import com.taxapprf.domain.cbr.CurrencyWithRateModel
import kotlinx.coroutines.flow.Flow

interface CBRRepository {
    suspend fun getCurrencyRate(currencyId: Int, date: Long): Double?
    fun getCurrencies(): Flow<List<CurrencyModel>>
    suspend fun getCurrenciesWithRate(date: Long): List<CurrencyWithRateModel>
}