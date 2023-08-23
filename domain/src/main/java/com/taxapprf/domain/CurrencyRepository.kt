package com.taxapprf.domain

import com.taxapprf.domain.currency.CurrencyModel
import kotlinx.coroutines.flow.Flow

interface CurrencyRepository {
    fun getTodayCurrency(date: String): Flow<List<CurrencyModel>>
}