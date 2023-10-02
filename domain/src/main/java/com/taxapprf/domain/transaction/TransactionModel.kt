package com.taxapprf.domain.transaction


data class TransactionModel(
    val id: Int,
    val name: String?,
    val date: Long,
    val typeK: Int,
    val sum: Double,
    val tax: Double?,

    val currencyId: Int,
    val currencyCharCode: String,
    val currencyRate: Double?,
)