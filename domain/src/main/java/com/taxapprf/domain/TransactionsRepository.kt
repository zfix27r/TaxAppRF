package com.taxapprf.domain

import com.taxapprf.domain.transactions.ReportModel
import com.taxapprf.domain.transactions.TransactionModel
import kotlinx.coroutines.flow.Flow

interface TransactionsRepository {
    fun observeReport(reportId: Int): Flow<ReportModel?>
    fun observeTransactions(reportId: Int): Flow<List<TransactionModel>>
}