package com.taxapprf.data

import android.net.Uri
import com.taxapprf.data.local.excel.ExcelDaoImpl
import com.taxapprf.data.local.room.dao.LocalTransactionDao
import com.taxapprf.data.local.room.entity.LocalTransactionEntity
import com.taxapprf.data.remote.firebase.FirebaseTransactionDaoImpl
import com.taxapprf.data.sync.SyncTransactions
import com.taxapprf.domain.NetworkManager
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
    private val networkManager: NetworkManager,
    private val localDao: LocalTransactionDao,
    private val remoteDao: FirebaseTransactionDaoImpl,
    private val excelDao: ExcelDaoImpl
) : TransactionRepository {
    override fun observeAll(accountKey: String, reportKey: String): Flow<List<TransactionModel>> =
        SyncTransactions(localDao, remoteDao, accountKey, reportKey).observeAll()

    override suspend fun save(saveTransactionModel: SaveTransactionModel) {
        with(saveTransactionModel) {
            rateCBRF?.let {
                if (it > 0) tax = (sum * it).roundUpToTwo()
            }
            localDao.save(toLocalTransactionEntity())
        }
    }

    override suspend fun delete(deleteTransactionModel: DeleteTransactionModel) {
        with(deleteTransactionModel) {
            val tax = reportTax - transactionTax
            val size = reportSize - 1

            if (networkManager.available)
                remoteDao.delete(accountKey, reportKey, transactionKey)
            else
                localDao.deleteDeferred(transactionKey)

//            updateOrDeleteReport(accountKey, reportKey, tax, size)
        }
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
}