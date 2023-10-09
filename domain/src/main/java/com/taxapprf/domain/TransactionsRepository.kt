package com.taxapprf.domain

interface TransactionsRepository {
    suspend fun deleteTransactions(transactionsId: List<Int>)

}