package com.taxapprf.domain.transaction

data class DeleteTransactionModel(
    val accountKey: String,
    val reportKey: String,

    val transactionKey: String,
    val transactionTax: Double?,

    val reportSize: Int,
    val reportTax: Double,
)