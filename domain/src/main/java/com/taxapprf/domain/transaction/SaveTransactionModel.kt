package com.taxapprf.domain.transaction

data class SaveTransactionModel(
    val transactionId: Int? = null,
    val reportId: Int? = null,

    val accountId: Int,
    val type: TransactionTypes,
    val currencyOrdinal: Int,

    val name: String,
    val date: Long,
    val sum: Double,

    val tax: Double? = null,
) {
    var newTransactionId: Int? = null
    var newReportId: Int? = null
}