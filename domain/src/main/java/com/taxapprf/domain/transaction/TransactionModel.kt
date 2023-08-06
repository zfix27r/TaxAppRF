package com.taxapprf.domain.transaction

data class TransactionModel(
    val key: String,
    val type: String,
    val id: String,
    val date: String,
    val currency: String,
    val rateCentralBank: Double,
    val sum: Double,
    val sumRub: Double
)