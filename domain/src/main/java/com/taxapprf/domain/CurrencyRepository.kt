package com.taxapprf.domain

import com.taxapprf.domain.currency.CurrencyModel
import kotlinx.coroutines.flow.Flow

interface CurrencyRepository {
    fun observe(date: String): Flow<List<CurrencyModel>>
    fun getCurrencyRate(date: String, currency: String): Double
}