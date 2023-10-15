package com.taxapprf.domain

interface TaxRepository {
    suspend fun updateAllEmptySumRUB()
    fun updateAllEmptyTaxRUB()
}