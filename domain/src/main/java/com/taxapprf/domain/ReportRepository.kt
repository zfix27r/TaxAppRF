package com.taxapprf.domain

import com.taxapprf.domain.delete.DeleteReportWithTransactionsModel
import com.taxapprf.domain.delete.DeleteTransactionWithReportModel
import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.update.UpdateReportWithTransactionTaxModel
import kotlinx.coroutines.flow.Flow

interface ReportRepository {
    fun observe(reportId: Int): Flow<ReportModel?>
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
    suspend fun deleteReport(deleteReportWithTransactionsModel: DeleteReportWithTransactionsModel): Int
    suspend fun deleteOrUpdateReport(deleteTransactionWithReportModel: DeleteTransactionWithReportModel)

    suspend fun saveReport(reportModel: ReportModel)
    suspend fun deleteAll()
}