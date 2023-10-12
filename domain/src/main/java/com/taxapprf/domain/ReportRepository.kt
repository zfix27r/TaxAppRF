package com.taxapprf.domain

import com.taxapprf.domain.transactions.ReportModel
import com.taxapprf.domain.update.UpdateReportWithTransactionTaxModel
import kotlinx.coroutines.flow.Flow

interface ReportRepository {

    fun observeAll(accountId: Int): Flow<List<ReportModel>>
    suspend fun updateWithCUDTransaction(
        accountId: Int,
        reportId: Int?,
        transactionId: Int?,
        date: Long,
        transactionTax: Double?
    ): Int

    suspend fun getReport(reportId: Int): ReportModel?
    suspend fun updateTax(updateReportWithTransactionTaxModel: UpdateReportWithTransactionTaxModel)

    suspend fun saveReport(reportModel: ReportModel)
    suspend fun deleteAll()
}