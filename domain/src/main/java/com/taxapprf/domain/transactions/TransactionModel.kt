package com.taxapprf.domain.transactions

import com.taxapprf.domain.currency.Currencies

data class TransactionModel(
    val id: Int,
    val name: String?,
    val date: Long,
    val sum: Double,
    val tax: Double?,
    val type: TransactionTypes,
    val currency: Currencies,
    val currencyRate: Double?,
)