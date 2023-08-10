package com.taxapprf.domain

import com.taxapprf.domain.currency.CurrencyModel
import kotlinx.coroutines.flow.Flow


interface CurrencyRepository {
    fun getTodayRate(date: String): Flow<List<CurrencyModel>>
}