package com.taxapprf.domain

import com.taxapprf.domain.taxes.TaxAdapterModel
import kotlinx.coroutines.flow.Flow

interface TaxRepository {
    fun getTaxes(accountName: String): Flow<List<TaxAdapterModel>>
    fun saveTaxesFromExcel(storagePath: String): Flow<Unit>

    fun getCBRRate(date: String, currency: String): Flow<Double>
}