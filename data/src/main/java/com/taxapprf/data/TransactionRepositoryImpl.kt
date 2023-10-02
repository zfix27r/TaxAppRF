package com.taxapprf.data

import com.taxapprf.data.local.room.LocalDeletedKeyDao
import com.taxapprf.data.local.room.LocalTransactionDao
import com.taxapprf.data.local.room.entity.LocalDeletedKeyEntity
import com.taxapprf.data.local.room.entity.LocalTransactionEntity
import com.taxapprf.data.local.room.model.GetExcelTransactionWithCurrency
import com.taxapprf.data.local.room.model.GetTransactionWithCurrency
import com.taxapprf.domain.TransactionRepository
import com.taxapprf.domain.delete.DeleteReportWithTransactionsModel
import com.taxapprf.domain.delete.DeleteTransactionWithReportModel
import com.taxapprf.domain.excel.ExcelTransactionModel
import com.taxapprf.domain.excel.ExportExcelModel
import com.taxapprf.domain.toAppDate
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.domain.transaction.TransactionTypeModel
import com.taxapprf.domain.update.UpdateReportWithTransactionTaxModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepositoryImpl @Inject constructor(
    private val localDao: LocalTransactionDao,
    private val localDeletedKeyDao: LocalDeletedKeyDao,
) : TransactionRepository {
    override fun observeReport(reportId: Int) =
        localDao.observeReport(reportId)
            .map { transitions -> transitions.map { it.toTransactionModel() } }

    override fun observe(transactionId: Int) =
        localDao.observe(transactionId)
            .map { it?.toTransactionModel() }

    override fun getTransactionTypes() =
        listOf(
            TransactionTypeModel(id = 1, k = 1),
            TransactionTypeModel(id = 2, k = 0),
            TransactionTypeModel(id = 3, k = -1),
        )

    override suspend fun inflateExcelTransactions(exportExcelModel: ExportExcelModel) =
        localDao.getExcelTransactionsWithCurrency(exportExcelModel.reportId)
            .map { it.toExcelTransactionModel(exportExcelModel) }

    override suspend fun deleteAll() =
        localDao.deleteAll()

    override suspend fun save(saveTransactionModel: SaveTransactionModel) =
        saveTransactionModel.toLocalTransactionEntity()?.let { localDao.save(it) }

    override suspend fun updateTax(updateReportWithTransactionTaxModel: UpdateReportWithTransactionTaxModel) {
        with(updateReportWithTransactionTaxModel) {
            tax?.let {
                localDao.updateTax(transactionId, it)
            }
        }
    }

    override suspend fun deleteTransaction(
        deleteTransactionWithUpdateReportModel: DeleteTransactionWithReportModel
    ) =
        with(deleteTransactionWithUpdateReportModel) {
            localDao.getTransactionKeys(transactionId)?.let { transactionKeys ->
                transactionKeys.transactionKey?.let { transactionKey ->
                    val deletedKey = LocalDeletedKeyEntity(
                        accountKey = transactionKeys.accountKey,
                        reportKey = transactionKeys.reportKey,
                        transactionKey = transactionKey,
                        syncAt = transactionKeys.syncAt
                    )
                    localDeletedKeyDao.save(deletedKey)
                }

                localDao.delete(transactionId)
                this.apply {
                    reportId = transactionKeys.reportId
                    transactionTax = transactionKeys.tax
                }
            } ?: run {
                null
            }
        }

    override suspend fun deleteReportTransactions(
        deleteReportWithTransactionsModel: DeleteReportWithTransactionsModel
    ) {
        localDao.getTransactionsKeys(deleteReportWithTransactionsModel.reportId)
            .map { transactionKeys ->

                transactionKeys.transactionKey?.let { transactionKey ->
                    val deletedKey = LocalDeletedKeyEntity(
                        accountKey = transactionKeys.accountKey,
                        reportKey = transactionKeys.reportKey,
                        transactionKey = transactionKey,
                        syncAt = transactionKeys.syncAt
                    )
                    localDeletedKeyDao.save(deletedKey)
                }

                localDao.delete(transactionKeys.id)
            }
    }

    private fun SaveTransactionModel.toLocalTransactionEntity(): LocalTransactionEntity? {
        val transactionId = transactionId ?: LocalTransactionEntity.DEFAULT_ID
        val reportId = newReportId ?: return null

        return LocalTransactionEntity(
            id = transactionId,
            accountId = accountId,
            reportId = reportId,
            currencyId = currencyId,
            name = name,
            date = date,
            type = type,
            sum = sum
        )
    }

    private fun GetTransactionWithCurrency.toTransactionModel() =
        TransactionModel(
            id,
            name,
            date,
            type,
            sum,
            tax,
            currencyId,
            currencyCharCode,
            currencyRate
        )

    private fun GetExcelTransactionWithCurrency.toExcelTransactionModel(
        exportExcelModel: ExportExcelModel
    ) =
        ExcelTransactionModel(
            name = name,
            appDate = date.toAppDate(),
            titleType = exportExcelModel.transactionTypes[type],
            sum = sum,
            currencyCharCode = currencyCharCode,
            currencyRate = currencyRate,
            tax = tax
        )
}