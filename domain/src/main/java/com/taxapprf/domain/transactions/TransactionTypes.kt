package com.taxapprf.domain.transactions


enum class TransactionTypes(
    val k: Int
) {
    TRADE(
        k = 1
    ),
    FUNDING_WITHDRAWAL(
        k = 0
    ),
    COMMISSION(
        k = -1
    )
}