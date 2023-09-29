package com.taxapprf.domain

import com.taxapprf.domain.delete.DeleteReportWithTransactionsModel
import com.taxapprf.domain.delete.DeleteTransactionWithReportModel
import com.taxapprf.domain.excel.ExcelTransactionModel
import com.taxapprf.domain.excel.ExportExcelModel
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.domain.transaction.TransactionTypeModel
import com.taxapprf.domain.update.UpdateReportWithTransactionTaxModel
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun observeReport(reportId: Int): Flow<List<TransactionModel>>
    fun observe(transactionId: Int): Flow<TransactionModel?>

    fun getTransactionTypes(): List<TransactionTypeModel>
    suspend fun save(saveTransactionModel: SaveTransactionModel): Long?
    suspend fun updateTax(updateReportWithTransactionTaxModel: UpdateReportWithTransactionTaxModel)
    suspend fun deleteTransaction(
        deleteTransactionWithUpdateReportModel: DeleteTransactionWithReportModel
    ): DeleteTransactionWithReportModel?

    suspend fun deleteReportTransactions(
        deleteReportWithTransactionsModel: DeleteReportWithTransactionsModel
    )

    suspend fun inflateExcelTransactions(exportExcelModel: ExportExcelModel): List<ExcelTransactionModel>
}