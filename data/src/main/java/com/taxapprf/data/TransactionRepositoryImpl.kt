package com.taxapprf.data

import com.taxapprf.data.error.DataErrorCBR
import com.taxapprf.data.remote.cbrapi.CBRAPI
import com.taxapprf.data.remote.firebase.FirebaseReportDaoImpl
import com.taxapprf.data.remote.firebase.FirebaseTransactionDaoImpl
import com.taxapprf.domain.TransactionRepository
import com.taxapprf.domain.report.DeleteReportModel
import com.taxapprf.domain.report.GetReportModel
import com.taxapprf.domain.report.SaveReportModel
import com.taxapprf.domain.transaction.DeleteTransactionModel
import com.taxapprf.domain.transaction.GetTransactionsModel
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.transaction.TransactionType
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.math.abs

class TransactionRepositoryImpl @Inject constructor(
    private val firebaseTransactionDao: FirebaseTransactionDaoImpl,
    private val firebaseReportDao: FirebaseReportDaoImpl,
    private val cbrapi: CBRAPI,
) : TransactionRepository {
    override fun getTransactions(getTransactionModel: GetTransactionsModel) =
        firebaseTransactionDao.getTransactions(getTransactionModel)

    override fun saveTransactionModel(saveTransactionModel: SaveTransactionModel) = flow {
        with(saveTransactionModel) {
            updateCBRRate()
            updateTax()

            updatePathTransaction()

            firebaseTransactionDao.saveTransaction(saveTransactionModel)
            emit(Unit)
        }
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

    private fun SaveTransactionModel.updateTax() {
        var newSum = sum
        val k = when (type) {
            TransactionType.COMMISSION.name -> 0.0
            TransactionType.FUNDING_WITHDRAWAL.name -> {
                newSum = abs(sum)
                -1.0
            }

            else -> 1.0
        }

        val newTax = newSum * rateCBR * 0.13 * k
        tax = newTax.roundUpToTwo()
    }
}