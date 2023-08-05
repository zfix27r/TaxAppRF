package com.taxapprf.data

import com.taxapprf.data.error.CBRErrorRateIsEmpty
import com.taxapprf.data.local.room.dao.TaxDao
import com.taxapprf.data.local.room.dao.TransactionDao
import com.taxapprf.data.local.room.entity.TaxEntity
import com.taxapprf.data.local.room.entity.TransactionEntity
import com.taxapprf.data.local.room.model.DeleteTransactionDataModel
import com.taxapprf.data.local.room.model.TaxWithTransactionsDataModel
import com.taxapprf.data.remote.cbrapi.CBRAPI
import com.taxapprf.data.remote.firebase.FirebaseAPI
import com.taxapprf.domain.FirebaseRequestModel
import com.taxapprf.domain.TransactionRepository
import com.taxapprf.domain.TransactionType
import com.taxapprf.domain.transaction.DeleteTransactionModel
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.domain.transaction.TransactionsModel
import com.taxapprf.domain.year.SaveYearSumModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject
import kotlin.math.abs

class TransactionRepositoryImpl @Inject constructor(
    private val firebase: FirebaseAPI,
    private val cbrapi: CBRAPI,
    private val taxDao: TaxDao,
    private val transactionDao: TransactionDao,
) : TransactionRepository {
    override fun getTransaction(transactionKey: String) =
        transactionDao.getTransaction(transactionKey).map { it.toTransactionModel() }

    private fun TransactionEntity.toTransactionModel() =
        TransactionModel(key, type, id, date, currency, rateCentralBank, sum, sumRub)

    override fun getTransactions(account: String, year: String) =
        transactionDao.getTransactions(account, year)
            .map { it.toTransactionsModel() }

    override fun saveTransactionModel(transaction: SaveTransactionModel) = flow {
        with(transaction) {
            updateCBRRate()
            updateSumRub()

            key?.let {
                if (isYearUpdated) {
                    val request = DeleteTransactionModel(account, year, it)
                    deleteTransaction(request)
                }
                firebase.updateTransaction(transaction)
            } ?: run {
                key = firebase.addTransaction(transaction)
                //transaction.updateFirebaseYear()
            }

            val sum = transactionDao.getTransactionsTax(account, year)
            val tax = TaxEntity("$account-$year", account, year, sum)
            taxDao.saveTax(tax)

            transactionDao.saveTransaction(toTransactionEntity())
        }
        emit(Unit)
    }

    override fun deleteTransaction(deleteTransactionModel: DeleteTransactionModel) = flow {
        deleteTransactionModel.deleteTransaction()
        emit(Unit)
    }

    private suspend fun DeleteTransactionModel.deleteTransaction() {
        firebase.deleteTransaction(this)
        transactionDao.deleteTransaction(this.transactionKey)
    }

    override fun deleteTransactions(deleteTransactionModel: DeleteTransactionModel) = flow {
        with(deleteTransactionModel) {
            firebase.deleteTransaction(deleteTransactionModel)
            val deleteTransaction = DeleteTransactionDataModel(account, year)
            transactionDao.deleteTransactions(deleteTransaction)
        }
        emit(Unit)
    }

    private fun SaveTransactionModel.updateCBRRate() {
        rateCentralBank = cbrapi.getCurrency(date).execute().body()
            ?.getCurrencyRate(currency)
            ?: throw CBRErrorRateIsEmpty()
    }

    override fun getYearSum(requestModel: FirebaseRequestModel) = flow {
        emit(firebase.getYearSum(requestModel))
    }

    override fun saveYearSum(saveYearSumModel: SaveYearSumModel) = flow<Unit> {
//        emit(firebase.saveYearSum(saveYearSumModel))
    }

    override fun deleteYearSum(requestModel: FirebaseRequestModel) = flow {
        emit(firebase.deleteYearSum(requestModel))
    }

    private fun Double.updateYearSum(): Double {
        /*        _transaction.value?.let { transaction ->
                    var big = BigDecimal(this - transaction.sumRub)
                    big = big.setScale(2, RoundingMode.HALF_UP)
                    return big.toDouble()
                }*/
        return 0.0
    }

    private fun SaveTransactionModel.updateSumRub() {
        val k = when (TransactionType.valueOf(type)) {
            TransactionType.FUNDING_WITHDRAWAL -> 0
            TransactionType.COMMISSION -> {
                sum = abs(sum)
                -1
            }

            else -> 1
        }

        var sumRubBigDecimal = BigDecimal(sum * rateCentralBank * 0.13 * k)
        sumRubBigDecimal = sumRubBigDecimal.setScale(2, RoundingMode.HALF_UP)
        sumRub = sumRubBigDecimal.toDouble()
    }

    private fun List<TaxWithTransactionsDataModel>.toTransactionsModel() =
        if (isNotEmpty()) {
            TransactionsModel(
                taxSum = filterNotNull().first().taxSum.toString(),
                transactions = map {
                    with(it) {
                        TransactionModel(
                            key, type, id, date, currency,
                            rateCentralBank, sum, sumRub
                        )
                    }
                }
            )
        } else null

    private fun SaveTransactionModel.toTransactionEntity() =
        TransactionEntity(
            key!!, account, year, type, id, date, currency,
            rateCentralBank, sum, sumRub
        )
}