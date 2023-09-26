package com.taxapprf.data

import android.net.Uri
import com.taxapprf.data.local.excel.ExcelDaoImpl
import com.taxapprf.data.local.room.LocalDeletedKeyDao
import com.taxapprf.data.local.room.LocalTransactionDao
import com.taxapprf.data.local.room.entity.LocalDeletedKeyEntity
import com.taxapprf.data.local.room.entity.LocalTransactionEntity
import com.taxapprf.data.local.room.model.GetTransactionWithCurrency
import com.taxapprf.domain.TransactionRepository
import com.taxapprf.domain.tax.UpdateTaxModel
import com.taxapprf.domain.transaction.DeleteTransactionModel
import com.taxapprf.domain.transaction.GetExcelToShareModel
import com.taxapprf.domain.transaction.GetExcelToStorageModel
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.transaction.SaveTransactionsFromExcelModel
import com.taxapprf.domain.transaction.TransactionModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepositoryImpl @Inject constructor(
    private val localDao: LocalTransactionDao,
    private val deletedKeyDao: LocalDeletedKeyDao,
    private val excelDao: ExcelDaoImpl
) : TransactionRepository {
    override fun observeAll(accountId: Int, reportId: Int) =
        localDao.observeAll(accountId, reportId)
            .map { transitions -> transitions.map { it.toTransactionModel() } }

    override suspend fun save(saveTransactionModel: SaveTransactionModel) =
        saveTransactionModel.toLocalTransactionEntity()?.let { localDao.save(it) }

    override suspend fun deleteTransactionStep1(deleteTransactionModel: DeleteTransactionModel) =
        with(deleteTransactionModel) {
            localDao.getTransactionKeys(transactionId)?.let { transactionKeys ->
                transactionKeys.transactionKey?.let { transactionKey ->
                    val deletedKey = LocalDeletedKeyEntity(
                        accountKey = transactionKeys.accountKey,
                        reportKey = transactionKeys.reportKey,
                        transactionKey = transactionKey,
                        syncAt = transactionKeys.syncAt
                    )
                    deletedKeyDao.save(deletedKey)
                }

                localDao.delete(transactionId)
                deleteTransactionModel.apply {
                    transactionTax = transactionKeys.tax
                }
            } ?: run {
                null
            }
        }

    override suspend fun deleteAll(accountId: Int, reportId: Int) =
        localDao.deleteAll(accountId, reportId)

    override suspend fun updateTax(updateTaxModel: UpdateTaxModel) {
        with(updateTaxModel) {
            tax?.let {
                localDao.updateTax(transactionId, it)
            }
        }
    }

    override fun getExcelToShare(getExcelToShareModel: GetExcelToShareModel): Flow<Uri> =
        flow {
            emit(excelDao.getExcelToShare(getExcelToShareModel))
        }

    override fun getExcelToStorage(getExcelToStorageModel: GetExcelToStorageModel): Flow<Uri> =
        flow {
            emit(excelDao.getExcelToStorage(getExcelToStorageModel))
        }

    override fun saveFromExcel(saveTransactionsFromExcelModel: SaveTransactionsFromExcelModel): Flow<Unit> =
        flow {
/*            excelDao.saveExcel(saveTransactionsFromExcelModel)
                .map { localDao.save(it) }*/

            emit(Unit)
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
}