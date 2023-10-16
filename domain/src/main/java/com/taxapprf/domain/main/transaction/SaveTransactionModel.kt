package com.taxapprf.domain.main.transaction

data class SaveTransactionModel(
    val accountId: Int,
    val reportId: Int? = null,
    val transactionId: Int? = null,

    val transactionTypeOrdinal: Int,
    val currencyOrdinal: Int,
    val name: String,
    val date: Long,
    val sum: Double,
    val tax: Double? = null,
)