package com.taxapprf.domain.transaction

data class GetTransactionModel(
    val accountKey: String,
    val reportKey: String,
    val transactionKey: String,
)