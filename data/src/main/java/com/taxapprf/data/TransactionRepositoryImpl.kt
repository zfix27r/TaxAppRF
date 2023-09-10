package com.taxapprf.data

import com.taxapprf.data.error.DataErrorCBR
import com.taxapprf.data.error.DataErrorConnection
import com.taxapprf.data.local.excel.ExcelDaoImpl
import com.taxapprf.data.local.room.dao.LocalTransactionDao
import com.taxapprf.data.remote.cbrapi.CBRAPI
import com.taxapprf.data.remote.firebase.FirebaseReportDaoImpl
import com.taxapprf.data.remote.firebase.FirebaseTransactionDaoImpl
import com.taxapprf.data.remote.firebase.model.GetReportModel
import com.taxapprf.data.sync.SyncTransactions
import com.taxapprf.domain.TransactionRepository
import com.taxapprf.domain.report.SaveReportModel
import com.taxapprf.domain.transaction.DeleteTransactionModel
import com.taxapprf.domain.transaction.GetExcelToShareModel
import com.taxapprf.domain.transaction.GetExcelToStorageModel
import com.taxapprf.domain.transaction.ObserveTransactionsModel
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.transaction.SaveTransactionsFromExcelModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val localTransactionDao: LocalTransactionDao,
    private val firebaseTransactionDao: FirebaseTransactionDaoImpl,
    private val firebaseReportDao: FirebaseReportDaoImpl,
    private val cbrapi: CBRAPI,
    private val excelDao: ExcelDaoImpl,
) : TransactionRepository {
    override fun observeTransactions(observeTransactionsModel: ObserveTransactionsModel) =
        SyncTransactions(
            localTransactionDao,
            firebaseTransactionDao,
            observeTransactionsModel.accountKey,
            observeTransactionsModel.reportKey
        ).observe()

    override fun saveTransaction(saveTransactionModel: SaveTransactionModel) = flow {
        saveTransactionModel.saveAndUpdatePath()
        firebaseTransactionDao.saveTransaction(saveTransactionModel)
        emit(Unit)
    }

    private suspend fun SaveTransactionModel.saveAndUpdatePath() {
        updateOldPathTransaction()

        updateCBRRate()
        tax = updateTax(sum, type, rateCBR)

        updatePathTransaction()
    }

    private suspend fun SaveTransactionModel.updateOldPathTransaction() {
        asDeleteTransactionModel()?.let {
            if (isReportYearChanged()) {
                firebaseTransactionDao.deleteTransaction(it)
                if (reportSize < 2)
                    firebaseReportDao.delete(accountKey, reportYear)
                else
                    decrementAndSave(accountKey, reportYear)
            }
        }
    }

    private suspend fun SaveTransactionModel.updatePathTransaction() {
        incrementAndSave(accountKey, yearKey)
    }

    private suspend fun SaveTransactionModel.decrementAndSave(
        accountKey: String,
        reportKey: String
    ) {
        val getReportModel = GetReportModel(accountKey, reportKey)

        firebaseReportDao.get(getReportModel).let {
            val newTax = (it.tax - tax).roundUpToTwo()
            val newSize = it.size - 1
            val saveReportModel = SaveReportModel(accountKey, reportKey, newTax, newSize)
            firebaseReportDao.save(saveReportModel)
        }
    }

    private suspend fun SaveTransactionModel.incrementAndSave(
        accountKey: String,
        yearKey: String
    ) {
        val getReportModel = GetReportModel(accountKey, yearKey)

        firebaseReportDao.get(getReportModel).let {
            val newTax = (it.tax + tax).roundUpToTwo()
            val newSize = it.size + 1
            val saveReportModel = SaveReportModel(accountKey, yearKey, newTax, newSize)
            firebaseReportDao.save(saveReportModel)
        }
    }

    override fun deleteTransaction(deleteTransactionModel: DeleteTransactionModel) = flow {
        with(deleteTransactionModel) {
            val newSize = reportSize - 1

            if (newSize == 0) {
                firebaseReportDao.delete(accountKey, reportKey)
            } else {
                val transactionTax = transactionTax ?: 0.0

                val newTax = (reportTax - transactionTax).roundUpToTwo()
                val saveReportModel = SaveReportModel(accountKey, reportKey, newTax, newSize)
                firebaseReportDao.save(saveReportModel)

                firebaseTransactionDao.deleteTransaction(deleteTransactionModel)
            }

            emit(Unit)
        }
    }

    override fun getExcelToShare(getExcelToShareModel: GetExcelToShareModel) = flow {
        emit(excelDao.getExcelToShare(getExcelToShareModel))
    }

    override fun getExcelToStorage(getExcelToStorageModel: GetExcelToStorageModel) = flow {
        emit(excelDao.getExcelToStorage(getExcelToStorageModel))
    }

    override fun saveTransactionsFromExcel(saveTransactionsFromExcelModel: SaveTransactionsFromExcelModel) =
        flow {
            excelDao.saveExcel(saveTransactionsFromExcelModel)
                .map { it.saveAndUpdatePath() }

            emit(Unit)
        }

    private fun SaveTransactionModel.updateCBRRate() {
        try {
            val request = cbrapi.getCurrency(date).execute()

            try {
                rateCBR = request.body()!!
                    .getCurrencyRate(currency)!!
                    .roundUpToTwo()
            } catch (_: Exception) {
                throw DataErrorCBR()
            }
        } catch (_: Exception) {
            throw DataErrorConnection()
        }
    }
}