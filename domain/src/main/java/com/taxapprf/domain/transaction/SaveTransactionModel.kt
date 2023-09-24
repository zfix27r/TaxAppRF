package com.taxapprf.domain.transaction

data class SaveTransactionModel(
    val id: Int? = null,

    val accountKey: String,
    val reportKey: String? = null,
    val transactionKey: String? = null,

    val newReportKey: String,

    val date: Long,
    val name: String,
    val currencyCharCode: String,
    val type: String,
    val sum: Double,

    var tax: Double? = null,
    var rateCBRF: Double? = null,
)