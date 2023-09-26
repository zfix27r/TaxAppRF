package com.taxapprf.domain

import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.tax.UpdateTaxModel
import com.taxapprf.domain.transaction.DeleteTransactionModel
import kotlinx.coroutines.flow.Flow

interface ReportRepository {
    fun observe(reportId: Int): Flow<ReportModel?>
    fun observeAll(accountId: Int): Flow<List<ReportModel>>
    suspend fun deleteAll(accountId: Int): Int
    suspend fun getReportId(accountId: Int, reportId: Int?, date: Long, transactionTax: Double?): Int
    suspend fun updateTax(updateTaxModel: UpdateTaxModel)
    suspend fun delete(deleteTransactionModel: DeleteTransactionModel)
}