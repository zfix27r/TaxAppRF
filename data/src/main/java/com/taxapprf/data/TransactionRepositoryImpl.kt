package com.taxapprf.data

import com.taxapprf.data.error.CBRErrorRateIsEmpty
import com.taxapprf.data.error.DataErrorResponseEmpty
import com.taxapprf.data.remote.cbrapi.CBRAPI
import com.taxapprf.data.remote.firebase.FirebaseReportDaoImpl
import com.taxapprf.data.remote.firebase.FirebaseTransactionDaoImpl
import com.taxapprf.data.remote.firebase.model.FirebaseReportModel
import com.taxapprf.domain.FirebasePathModel
import com.taxapprf.data.remote.firebase.model.FirebaseTransactionModel
import com.taxapprf.domain.TransactionRepository
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.domain.transaction.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.math.abs

class TransactionRepositoryImpl @Inject constructor(
    private val firebaseTransactionDao: FirebaseTransactionDaoImpl,
    private val firebaseReportDao: FirebaseReportDaoImpl,
    private val cbrapi: CBRAPI,
) : TransactionRepository {
    override fun getTransaction(firebasePathModel: FirebasePathModel) = flow {
        emit(
            firebaseTransactionDao.getTransaction(firebasePathModel)
                ?.toTransactionModel()
        )
    }

    override fun getTransactions(firebasePathModel: FirebasePathModel) = flow {
        emit(
            firebaseTransactionDao.getTransactions(firebasePathModel)
                .map { it.toTransactionModel() }
        )
    }

    private fun FirebaseTransactionModel.toTransactionModel() =
        TransactionModel(
            key = key ?: throw DataErrorResponseEmpty(),
            name = name ?: "",
            date = date ?: "",
            type = type ?: "",
            currency = currency ?: "",
            rateCBR = rateCBR ?: 0L,
            sum = sum ?: 0L,
            tax = 0L
        )


    override fun saveTransactionModel(saveTransactionModel: SaveTransactionModel) = flow {
        with(saveTransactionModel) {
            val firebasePathModel = saveTransactionModel.toFirebasePathModel()

            transactionKey?.let {
                if (year != yearOld) {
                    updateCBRRate()
                    firebaseTransactionDao.deleteTransaction(firebasePathModel)
                }
            }

            updateTax()

            val oldTax = firebaseReportDao.getReportTax(firebasePathModel)?.tax ?: 0L
            val firebaseReportModel = FirebaseReportModel(year, oldTax + tax)
            firebaseReportDao.saveReportTax(firebasePathModel, firebaseReportModel)

            firebaseTransactionDao.saveTransaction(
                firebasePathModel,
                FirebaseTransactionModel().from(saveTransactionModel)
            )

            emit(Unit)
        }
    }

    private fun SaveTransactionModel.toFirebasePathModel() =
        FirebasePathModel(accountName, year, transactionKey)

    override fun deleteTransaction(firebasePathModel: FirebasePathModel): Flow<Unit> =
        flow {
            emit(firebaseTransactionDao.deleteTransaction(firebasePathModel))
        }

    private fun SaveTransactionModel.updateCBRRate() {
        val rate = cbrapi.getCurrency(date).execute().body()
            ?.getCurrencyRate(currency)
            ?: throw CBRErrorRateIsEmpty()
        rateCBR = (rate * 1000).toLong()
    }

    private fun SaveTransactionModel.updateTax() {
        val k = when (TransactionType.valueOf(type)) {
            TransactionType.FUNDING_WITHDRAWAL -> 0
            TransactionType.COMMISSION -> {
                sum = abs(sum)
                -1
            }

            else -> 1
        }

        tax = sum * rateCBR * 100 / 13 * k
    }
}