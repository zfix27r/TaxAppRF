package com.taxapprf.domain.transaction

data class SaveTransactionModel(
    val transactionId: Int? = null,
    val reportId: Int? = null,

    val accountId: Int,
    val currencyId: Int,

    val name: String,
    val date: Long,
    val type: Int,
    val sum: Double,

    val tax: Double? = null,
) {
    var newTransactionId: Int? = null
    var newReportId: Int? = null
}