package com.taxapprf.data

import com.taxapprf.data.error.CBRErrorRateIsEmpty
import com.taxapprf.data.local.room.dao.TransactionDao
import com.taxapprf.data.local.room.entity.TransactionEntity
import com.taxapprf.data.local.room.model.TaxWithTransactionsDataModel
import com.taxapprf.data.remote.cbrapi.CBRAPI
import com.taxapprf.data.remote.firebase.FirebaseAPI
import com.taxapprf.domain.FirebaseRequestModel
import com.taxapprf.domain.TransactionRepository
import com.taxapprf.domain.TransactionType
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
    private val transactionDao: TransactionDao,
) : TransactionRepository {
    override fun getTransaction(key: String) =
        transactionDao.getTransaction(key).map { it.toTransactionModel() }

    private fun TransactionEntity.toTransactionModel() =
        TransactionModel(key, type, id, date, currency, rateCentralBank, sum, sumRub)

    override fun getTransactions(account: String, year: String) =
        transactionDao.getTransactions(account, year)
            .map { it.toTransactionsModel() }

    override fun saveTransactionModel(transaction: SaveTransactionModel) =
        flow {
/*      getRateCentralBank.execute(saveTransaction.date, saveTransaction.currency)
            .onStart { loading() }
            .catch { error(it) }
            .collectLatest {
                saveTransaction.rateCentralBank = it
                saveTransaction.calculateSumRub()
                saveTransactionUseCase.execute(saveTransaction)
                    .onStart { loading() }
                    .catch { error(it) }
                    .collectLatest { success(BaseState.Edited) }
            }*/

            transaction.updateCBRRate()
            transaction.updateSumRub()

            transaction.key?.let {
                if (transaction.isYearUpdated) firebase.deleteTransaction(transaction)
                firebase.updateTransaction(transaction)
            } ?: run {
                val key = firebase.addTransaction(transaction)
                transaction.key = key
                //transaction.updateFirebaseYear()
            }

            transactionDao.saveTransaction(transaction.toTransactionEntity())
            emit(Unit)
        }

    private fun SaveTransactionModel.updateCBRRate() =
        cbrapi.getCurrency(date).execute()
            .body()?.getCurrencyRate(currency)
            ?: throw CBRErrorRateIsEmpty()

    override fun deleteTransaction(requestModel: FirebaseRequestModel) = flow {
        // emit(firebase.deleteTransaction(requestModel))
        emit(Unit)
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
            TransactionType.TRADE -> 1
            TransactionType.FUNDING_WITHDRAWAL -> 0
            TransactionType.COMMISSION -> {
                sum = abs(sum)
                -1
            }
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