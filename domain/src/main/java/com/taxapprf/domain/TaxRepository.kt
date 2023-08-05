package com.taxapprf.domain

import com.taxapprf.domain.taxes.DeleteTaxModel
import com.taxapprf.domain.taxes.TaxAdapterModel
import kotlinx.coroutines.flow.Flow

interface TaxRepository {
    fun getTaxes(request: FirebaseRequestModel): Flow<List<TaxAdapterModel>>
    fun saveTaxesFromExcel(storagePath: String): Flow<Unit>
    fun deleteTax(request: FirebaseRequestModel): Flow<Unit>
    fun getCBRRate(date: String, currency: String): Flow<Double>
}