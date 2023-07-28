package com.taxapprf.domain

import com.taxapprf.domain.taxes.TaxesAdapterModel
import kotlinx.coroutines.flow.Flow

interface TaxesRepository {
    fun getTaxes(): Flow<List<TaxesAdapterModel>>
    fun saveTaxesFromExcel(storagePath: String): Flow<Unit>

    fun getCBRRate(date: String, currency: String): Flow<Double>
}