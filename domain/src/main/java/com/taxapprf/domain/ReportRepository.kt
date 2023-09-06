package com.taxapprf.domain

import com.taxapprf.domain.report.DeleteWithTransactionsModel
import com.taxapprf.domain.report.ObserveReportsModel
import com.taxapprf.domain.report.ObserveReportModel
import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.report.SaveReportModel
import kotlinx.coroutines.flow.Flow

interface ReportRepository {
    fun observe(observeReportModel: ObserveReportModel): Flow<List<ReportModel>>
    fun get(accountKey: String, reportKey: String): ReportModel?
    fun save(saveReportModel: SaveReportModel): Flow<Unit>
    fun observeAll(observeReportsModel: ObserveReportsModel): Flow<List<ReportModel>>
    fun deleteWithTransactions(deleteReportModel: DeleteWithTransactionsModel): Flow<Unit>
    fun deleteWithTransactions(accountKey: String, reportKey: String): Flow<Unit>
    suspend fun deleteTransaction(accountKey: String, reportKey: String)
    suspend fun deleteDeferredTransaction(accountKey: String, reportKey: String)
}