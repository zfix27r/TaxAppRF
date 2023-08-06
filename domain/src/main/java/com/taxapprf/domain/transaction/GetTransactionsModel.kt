package com.taxapprf.domain.transaction

data class GetTransactionsModel(
    val accountKey: String,
    val reportKey: String,
)