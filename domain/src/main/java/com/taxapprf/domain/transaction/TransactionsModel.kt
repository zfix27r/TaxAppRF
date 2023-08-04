package com.taxapprf.domain.transaction

data class TransactionsModel(
    val taxSum: String,
    val transactions: List<TransactionModel>
)