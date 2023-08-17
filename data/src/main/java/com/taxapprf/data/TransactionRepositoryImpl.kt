package com.taxapprf.data

import com.taxapprf.data.error.DataErrorCBR
import com.taxapprf.data.error.internal.DataErrorInternalTransactionKeyEmpty
import com.taxapprf.data.remote.cbrapi.CBRAPI
import com.taxapprf.data.remote.firebase.FirebaseReportDaoImpl
import com.taxapprf.data.remote.firebase.FirebaseTransactionDaoImpl
import com.taxapprf.data.remote.firebase.model.FirebaseReportModel
import com.taxapprf.domain.TransactionRepository
import com.taxapprf.domain.report.DeleteReportModel
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
            val accountKey = accountKey ?: throw DataErrorInternalTransactionKeyEmpty()

            updateCBRRate()

            val year = date.getYearFromDate()

            transactionKey?.let { transactionKey ->
                yearKey?.let { yearKey ->
                    if (year != yearKey) {
                        val deleteTransactionModel =
                            DeleteTransactionModel(accountKey, year, transactionKey, 0.0, 0.0)
                        firebaseTransactionDao.deleteTransaction(deleteTransactionModel)
                    }
                }
            }

            yearKey = year
            updateTax()

            val newTax = (reportTax + tax).roundUpToTwo()
            val firebaseReportModel = FirebaseReportModel(year, newTax, ++reportSize)
            firebaseReportDao.saveReportTax(accountKey, year, firebaseReportModel)

            firebaseTransactionDao.saveTransaction(saveTransactionModel)

            emit(Unit)
        }
    }

    private fun String.getYearFromDate() = split("/")[2]

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
            ?: throw DataErrorCBR()
        rateCBR = rate.roundUpToTwo()
    }

    private fun SaveTransactionModel.updateTax() {
        val k = when (type) {
            TransactionType.COMMISSION.name -> 0.0
            TransactionType.FUNDING_WITHDRAWAL.name -> {
                sum = abs(sum)
                -1.0
            }

            else -> 1.0
        }

        rateCBR?.let { rate ->
            val calculateTax = sum * rate * 0.13 * k
            tax = calculateTax.roundUpToTwo()
        }
    }
}