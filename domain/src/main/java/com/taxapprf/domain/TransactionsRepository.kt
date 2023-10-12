package com.taxapprf.domain

import com.taxapprf.domain.transactions.ReportModel
import kotlinx.coroutines.flow.Flow

interface TransactionsRepository {
    fun observeReport(reportId: Int): Flow<ReportModel?>
}