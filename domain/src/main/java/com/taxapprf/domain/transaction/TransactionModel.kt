package com.taxapprf.domain.transaction

data class TransactionModel(
    val key: String,
    val name: String,
    val date: String,
    val type: Int,
    val currency: String,
    val rateCBR: Double,
    val sum: Double,
    val tax: Double,
)