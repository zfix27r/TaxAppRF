package com.taxapprf.domain

interface TaxRepository {
    suspend fun updateAllEmptySumRUBAndTaxRUB()
}