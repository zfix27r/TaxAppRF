package com.taxapprf.domain

interface DeletedRepository {
    suspend fun deleteReports(reportIds: List<Int>)
    suspend fun deleteTransactions(transactionIds: List<Int>)
}