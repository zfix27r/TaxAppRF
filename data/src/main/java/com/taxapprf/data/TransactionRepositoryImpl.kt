package com.taxapprf.data

import com.taxapprf.data.error.DataErrorCBR
import com.taxapprf.data.local.excel.ExcelDaoImpl
import com.taxapprf.data.remote.cbrapi.CBRAPI
import com.taxapprf.data.remote.firebase.FirebaseReportDaoImpl
import com.taxapprf.data.remote.firebase.FirebaseTransactionDaoImpl
import com.taxapprf.domain.TransactionRepository
import com.taxapprf.domain.report.DeleteReportModel
import com.taxapprf.domain.report.GetReportModel
import com.taxapprf.domain.report.SaveReportModel
import com.taxapprf.domain.transaction.SaveTransactionsFromExcelModel
import com.taxapprf.domain.transaction.DeleteTransactionModel
import com.taxapprf.domain.transaction.GetExcelToShareModel
import com.taxapprf.domain.transaction.GetExcelToStorageModel
import com.taxapprf.domain.transaction.GetTransactionsModel
import com.taxapprf.domain.transaction.SaveTransactionModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val firebaseTransactionDao: FirebaseTransactionDaoImpl,
    private val firebaseReportDao: FirebaseReportDaoImpl,
    private val cbrapi: CBRAPI,
    private val excelDao: ExcelDaoImpl,
) : TransactionRepository {
    override fun getTransactions(getTransactionModel: GetTransactionsModel) =
        firebaseTransactionDao.getTransactions(getTransactionModel)

    override fun saveTransaction(saveTransactionModel: SaveTransactionModel) = flow {
        saveTransactionModel.saveAndUpdatePath()
        emit(Unit)
    }

    private suspend fun SaveTransactionModel.saveAndUpdatePath() {
        updateCBRRate()
        tax = updateTax(sum, type, rateCBR)

        updatePathTransaction()

        firebaseTransactionDao.saveTransaction(this)
    }

    private suspend fun SaveTransactionModel.updatePathTransaction() {
        toDeleteTransactionModel()?.let {
            if (isReportYearChanged())
                firebaseTransactionDao.deleteTransaction(it)
        }

        incrementAndSave(accountKey, yearKey)
    }

    override fun deleteTransaction(deleteTransactionModel: DeleteTransactionModel) = flow {
        with(deleteTransactionModel) {
            val newSize = reportSize - 1

            if (newSize == 0) {
                val deleteReportModel = DeleteReportModel(accountKey, yearKey)
                firebaseReportDao.deleteReport(deleteReportModel)
            } else {
                val newTax = (reportTax - transactionTax).roundUpToTwo()

                val saveReportModel = SaveReportModel(accountKey, yearKey, newTax, newSize)
                firebaseReportDao.saveReport(saveReportModel)
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

    private suspend fun SaveTransactionModel.incrementAndSave(
        accountKey: String,
        yearKey: String
    ) {
        val getReportModel = GetReportModel(accountKey, yearKey)

        firebaseReportDao.getReport(getReportModel).let {
            val newTax = (it.tax + tax).roundUpToTwo()
            val newSize = it.size + 1
            val saveReportModel = SaveReportModel(accountKey, yearKey, newTax, newSize)
            firebaseReportDao.saveReport(saveReportModel)
        }
    }

    private fun SaveTransactionModel.updateCBRRate() {
        val rate = cbrapi.getCurrency(date).execute().body()
            ?.getCurrencyRate(currency)
            ?: throw DataErrorCBR()
        rateCBR = rate.roundUpToTwo()
    }
}