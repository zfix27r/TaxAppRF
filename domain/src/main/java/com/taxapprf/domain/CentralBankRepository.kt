package com.taxapprf.domain

import kotlinx.coroutines.flow.Flow

interface CentralBankRepository {
    fun getRate(date: String, currency: String): Flow<Double>
}