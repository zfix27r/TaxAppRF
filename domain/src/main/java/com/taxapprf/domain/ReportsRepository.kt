package com.taxapprf.domain

import com.taxapprf.domain.transactions.ReportModel
import kotlinx.coroutines.flow.Flow

interface ReportsRepository {
    fun observeAccountReports(accountId: Int): Flow<List<ReportModel>>
}