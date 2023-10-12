package com.taxapprf.domain

import com.taxapprf.domain.excel.ExcelTransactionModel
import com.taxapprf.domain.excel.ExportExcelModel
import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.domain.update.UpdateReportWithTransactionTaxModel
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun observeReport(reportId: Int): Flow<List<TransactionModel>>

    suspend fun updateTax(updateReportWithTransactionTaxModel: UpdateReportWithTransactionTaxModel)

    suspend fun inflateExcelTransactions(exportExcelModel: ExportExcelModel): List<ExcelTransactionModel>
    suspend fun deleteAll()
}