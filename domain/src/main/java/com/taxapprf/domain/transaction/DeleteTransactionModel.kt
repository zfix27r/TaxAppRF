package com.taxapprf.domain.transaction

data class DeleteTransactionModel(
    val transactionId: Int,
    val reportId: Int,
) {
    var transactionTax: Double? = null
}