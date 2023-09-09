package com.taxapprf.domain.transaction


data class TransactionModel(
    val id: Int,
    val transactionKey: String,
    val name: String?,
    val date: String,
    val type: String,
    val currency: String,
    val rateCBRF: Double,
    val sum: Double,
    val tax: Double,
    val isSync: Boolean,
    val isDelete: Boolean,
    val syncAt: Long,
)