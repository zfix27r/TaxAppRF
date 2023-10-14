package com.taxapprf.domain.main.transaction

data class UpdateTransactionTaxModel(
    val transactionId: Int,
    val currencyOrdinal: Int,
    val date: Long,
) {
    var rate: Double? = null
}