package com.taxapprf.domain.transaction


fun Map<Int, String>.find(title: String): Int? =
    entries.find { it.value == title }?.key

enum class TransactionType(
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