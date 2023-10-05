package com.taxapprf.domain.transaction

import com.taxapprf.domain.cbr.Currencies


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