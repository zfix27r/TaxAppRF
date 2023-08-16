package com.taxapprf.data

import com.taxapprf.data.error.cbr.DataErrorCBRRateIsEmpty
import com.taxapprf.data.error.internal.DataErrorTransactionKeyIsEmpty
import com.taxapprf.data.remote.cbrapi.CBRAPI
import com.taxapprf.data.remote.firebase.FirebaseReportDaoImpl
import com.taxapprf.data.remote.firebase.FirebaseTransactionDaoImpl
import com.taxapprf.data.remote.firebase.model.FirebaseReportModel
import com.taxapprf.domain.TransactionRepository
import com.taxapprf.domain.report.DeleteReportModel
import com.taxapprf.domain.transaction.DeleteTransactionModel
import com.taxapprf.domain.transaction.GetTransactionsModel
import com.taxapprf.domain.transaction.SaveTransactionModel
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
            val accountKey = accountKey ?: throw DataErrorTransactionKeyIsEmpty()

            updateCBRRate()

            transactionKey?.let {

                    if (year != yearOld) {
                    val deleteTransactionModel = DeleteTransactionModel(accountKey, year, it)
                    firebaseTransactionDao.deleteTransaction(deleteTransactionModel)
                }
            }

            updateTax()

            val oldTax = firebaseReportDao.getReportTax(accountKey, year)?.tax ?: 0.0
            val newTax = (oldTax + tax).roundUpToTwo()
            val firebaseReportModel = FirebaseReportModel(year, newTax)
            firebaseReportDao.saveReportTax(accountKey, year, firebaseReportModel)

            firebaseTransactionDao.saveTransaction(saveTransactionModel)

            emit(Unit)
        }
    }

    getYearFrom
    override fun deleteTransaction(deleteTransactionModel: DeleteTransactionModel) = flow {
        with(deleteTransactionModel) {
            if (reportTax - transactionTax == 0.0) {
                val deleteReportModel = DeleteReportModel(accountKey, yearKey)
                firebaseReportDao.deleteReport(deleteReportModel)
            }
        }

        emit(firebaseTransactionDao.deleteTransaction(deleteTransactionModel))
    }

    private fun SaveTransactionModel.updateCBRRate() {
        val rate = cbrapi.getCurrency(date).execute().body()
            ?.getCurrencyRate(currency)
            ?: throw DataErrorCBRRateIsEmpty()
        rateCBR = rate.roundUpToTwo()
    }

    private fun SaveTransactionModel.updateTax() {
        if (type == 0) tax = 0.0
        else {
            rateCBR?.let { rate ->
                if (type == -1) sum = abs(sum)

                val calculateTax = sum * rate * 0.13 * type
                tax = calculateTax.roundUpToTwo()
            }
        }
    }
}