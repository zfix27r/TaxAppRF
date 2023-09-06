package com.taxapprf.domain.transaction

data class SaveTransactionModel(
    val accountKey: String,
    val reportKey: String? = null,
    val transactionKey: String? = null,

    val date: String,
    val name: String,
    val currency: String,
    val type: String,
    val sum: Double,

    val tax: Double? = null,
    val rateCBR: Double? = null,
)