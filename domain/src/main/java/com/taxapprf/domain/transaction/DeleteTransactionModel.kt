package com.taxapprf.domain.transaction

data class DeleteTransactionModel(
    val account: String,
    val year: String,
    val transactionKey: String,
)