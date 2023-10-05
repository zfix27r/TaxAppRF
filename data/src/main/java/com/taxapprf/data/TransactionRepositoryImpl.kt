package com.taxapprf.data

import com.taxapprf.data.local.room.LocalDatabase.Companion.DEFAULT_ID
import com.taxapprf.data.local.room.LocalDeletedKeyDao
import com.taxapprf.data.local.room.LocalTransactionDao
import com.taxapprf.data.local.room.entity.LocalDeletedKeyEntity
import com.taxapprf.data.local.room.entity.LocalTransactionEntity
import com.taxapprf.data.local.room.model.GetExcelTransaction
import com.taxapprf.data.local.room.model.GetTransaction
import com.taxapprf.domain.TransactionRepository
import com.taxapprf.domain.cbr.Currencies
import com.taxapprf.domain.delete.DeleteReportWithTransactionsModel
import com.taxapprf.domain.delete.DeleteTransactionWithReportModel
import com.taxapprf.domain.excel.ExcelTransactionModel
import com.taxapprf.domain.excel.ExportExcelModel
import com.taxapprf.domain.toAppDate
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.domain.transaction.TransactionTypes
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

    override suspend fun inflateExcelTransactions(exportExcelModel: ExportExcelModel) =
        localDao.getExcelTransactionsWithCurrency(exportExcelModel.reportId)
            .map { it.toExcelTransactionModel() }

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
            localDao.getKeysTransaction(transactionId)?.let { transactionKeys ->
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
        localDao.getKeysTransactions(deleteReportWithTransactionsModel.reportId)
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
        val transactionId = transactionId ?: DEFAULT_ID
        val reportId = newReportId ?: return null

        return LocalTransactionEntity(
            id = transactionId,
            reportId = reportId,
            typeOrdinal = type.ordinal,
            currencyOrdinal = currencyOrdinal,
            name = name,
            date = date,
            sum = sum
        )
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