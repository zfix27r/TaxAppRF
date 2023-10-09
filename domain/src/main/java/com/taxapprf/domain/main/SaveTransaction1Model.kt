package com.taxapprf.domain.main

data class SaveTransaction1Model(
    val accountId: Int,
    val reportId: Int? = null,
    val transactionId: Int? = null,

    val transactionTypeOrdinal: Int,
    val currencyOrdinal: Int,
    val name: String,
    val date: Long,
    val sum: Double,
    val tax: Double? = null,
) {
    var rate: Double? = null
}