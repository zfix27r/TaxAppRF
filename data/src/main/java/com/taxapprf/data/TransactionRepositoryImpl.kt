package com.taxapprf.data

import com.taxapprf.data.local.room.LocalDeletedDao
import com.taxapprf.data.local.room.LocalTransactionDao
import com.taxapprf.data.local.room.model.GetExcelTransaction
import com.taxapprf.data.local.room.model.GetTransaction
import com.taxapprf.domain.TransactionRepository
import com.taxapprf.domain.cbr.Currencies
import com.taxapprf.domain.excel.ExcelTransactionModel
import com.taxapprf.domain.excel.ExportExcelModel
import com.taxapprf.domain.toAppDate
import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.domain.transaction.TransactionTypes
import com.taxapprf.domain.update.UpdateReportWithTransactionTaxModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepositoryImpl @Inject constructor(
    private val localDao: LocalTransactionDao,
    private val localDeletedKeyDao: LocalDeletedDao,
) : TransactionRepository {
    override fun observeReport(reportId: Int) =
        localDao.observeReport(reportId)
            .map { transitions -> transitions.map { it.toTransactionModel() } }

    override suspend fun inflateExcelTransactions(exportExcelModel: ExportExcelModel) =
        localDao.getExcelTransactionsWithCurrency(exportExcelModel.reportId)
            .map { it.toExcelTransactionModel() }

    override suspend fun deleteAll() =
        localDao.deleteAll()

    override suspend fun updateTax(updateReportWithTransactionTaxModel: UpdateReportWithTransactionTaxModel) {
        with(updateReportWithTransactionTaxModel) {
            tax?.let {
                localDao.updateTax(transactionId, it)
            }
        }
    }

    private fun GetTransaction.toTransactionModel() =
        TransactionModel(
            id,
            name,
            date,
            sum,
            tax,
            TransactionTypes.values()[typeOrdinal],
            Currencies.values()[currencyOrdinal],
            currencyRate
        )

    private fun GetExcelTransaction.toExcelTransactionModel() =
        ExcelTransactionModel(
            name = name,
            appDate = date.toAppDate(),
            typeName = TransactionTypes.values()[typeOrdinal].name,
            sum = sum,
            currencyCharCode = Currencies.values()[currencyOrdinal].name,
            currencyRate = currencyRate,
            tax = tax
        )
}