package com.taxapprf.domain.transaction.detail


data class TransactionDetailModel(
    val date: Long?,
    val name: String?,
    val transactionTypeOrdinal: Int?,
    val currencyOrdinal: Int?,
    val sum: Double?,
    val tax: Double?,
)