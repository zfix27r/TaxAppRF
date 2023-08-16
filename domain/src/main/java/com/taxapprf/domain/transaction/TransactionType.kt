package com.taxapprf.domain.transaction

enum class TransactionType(val k: Int) {
    TRADE(1),
    FUNDING_WITHDRAWAL(0),
    COMMISSION(-1)
}