package com.taxapprf.data

import android.net.Uri
import com.taxapprf.data.local.excel.ExcelDaoImpl
import com.taxapprf.data.local.room.LocalTransactionDao
import com.taxapprf.data.local.room.entity.LocalTransactionEntity
import com.taxapprf.data.remote.firebase.FirebaseTransactionDaoImpl
import com.taxapprf.domain.TransactionRepository
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
    private val remoteDao: FirebaseTransactionDaoImpl,
    private val excelDao: ExcelDaoImpl
) : TransactionRepository {
    override fun observeAll(accountKey: String, reportKey: String) =
        localDao.observeAll(accountKey, reportKey)
            .map { transitions -> transitions.map { it.toTransactionModel() } }

    override suspend fun save(saveTransactionModel: SaveTransactionModel) {
        with(saveTransactionModel) {
            rateCBRF?.let {
                if (it > 0) tax = (sum * it).roundUpToTwo()
            }
            localDao.save(toLocalTransactionEntity())
        }
    }

    override suspend fun delete(deleteTransactionModel: DeleteTransactionModel) {
        localDao.deleteDeferred(deleteTransactionModel.id)
    }

    override suspend fun deleteAll(accountKey: String, reportKey: String) {
        localDao.deleteAllDeferred(accountKey, reportKey)
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
            excelDao.saveExcel(saveTransactionsFromExcelModel)
                .map { localDao.save(it) }

            emit(Unit)
        }

    private fun SaveTransactionModel.toLocalTransactionEntity(
        newRateCBRF: Double? = null,
        newTax: Double? = null
    ) =
        LocalTransactionEntity(
            id = id ?: 0,
            accountKey = accountKey,
            reportKey = newReportKey,
            key = transactionKey ?: "",
            name = name,
            date = date,
            type = type,
            currency = currency,
            rateCBRF = newRateCBRF ?: rateCBRF ?: 0.0,
            sum = sum,
            tax = newTax ?: tax ?: 0.0,
            isSync = false,
            isDelete = false,
            syncAt = getTime()
        )

    private fun LocalTransactionEntity.toTransactionModel() =
        TransactionModel(id, key, name, date, type, currency, rateCBRF, sum, tax)
}