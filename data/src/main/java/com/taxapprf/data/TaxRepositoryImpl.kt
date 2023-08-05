package com.taxapprf.data

import com.taxapprf.data.local.room.dao.TaxDao
import com.taxapprf.data.local.room.dao.TransactionDao
import com.taxapprf.data.local.room.entity.TaxEntity
import com.taxapprf.data.local.room.entity.TransactionEntity
import com.taxapprf.data.local.room.model.DeleteTaxDataModel
import com.taxapprf.data.local.room.model.DeleteTransactionDataModel
import com.taxapprf.data.remote.cbrapi.CBRAPI
import com.taxapprf.data.remote.firebase.FirebaseAPI
import com.taxapprf.data.remote.firebase.FirebaseAPI.Companion.getAsDouble
import com.taxapprf.data.remote.firebase.FirebaseAPI.Companion.getAsString
import com.taxapprf.domain.FirebaseRequestModel
import com.taxapprf.domain.TaxRepository
import com.taxapprf.domain.TransactionType
import com.taxapprf.domain.taxes.TaxAdapterModel
import com.taxapprf.domain.transaction.SaveTransactionModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEmpty
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject
import kotlin.math.abs

class TaxRepositoryImpl @Inject constructor(
    private val firebaseAPI: FirebaseAPI,
    private val taxDao: TaxDao,
    private val transactionDao: TransactionDao,
    private val cbrapi: CBRAPI,
) : TaxRepository {
    override fun getTaxes(request: FirebaseRequestModel) = taxDao.getTaxes(request.account)
        .onEmpty { getAndSaveFirebaseAccountData(request) }
        .map { it.toListTaxAdapterModel() }

    private suspend fun getAndSaveFirebaseAccountData(request: FirebaseRequestModel) {
        firebaseAPI.getTaxes(request)
            .map { year ->
                var sumTaxes = 0.0
                val transactions = year
                    .child(FirebaseAPI.TRANSACTIONS)
                    .children
                    .mapNotNull { transaction ->
                        year.key?.let {
                            val tr = TransactionEntity(
                                key = transaction.getAsString(FirebaseAPI.KEY_TRANSACTION_KEY),
                                account = request.account,
                                year = it,
                                type = transaction.getAsString(FirebaseAPI.KEY_TRANSACTION_TYPE),
                                id = transaction.getAsString(FirebaseAPI.KEY_TRANSACTION_ID),
                                date = transaction.getAsString(FirebaseAPI.KEY_TRANSACTION_DATE),
                                currency = transaction.getAsString(FirebaseAPI.KEY_TRANSACTION_CURRENCY),
                                rateCentralBank = transaction.getAsDouble(FirebaseAPI.KEY_TRANSACTION_RATE_CENTRAL_BANK),
                                sum = transaction.getAsDouble(FirebaseAPI.KEY_TRANSACTION_SUM),
                                sumRub = transaction.getAsDouble(FirebaseAPI.KEY_TRANSACTION_SUM_RUB),
                            )

                            sumTaxes += tr.sumRub
                            tr
                        }
                    }
                transactionDao.saveTransactions(transactions)
                val tax = TaxEntity(0, request.account, year.toString(), sumTaxes)
                taxDao.saveTax(tax)
            }
    }

    override fun saveTaxesFromExcel(storagePath: String) = flow<Unit> {
/*        ExcelParcel(storagePath)
            .parse()
            .map { transaction ->
                getCBRRate(transaction.date, transaction.currency).collectLatest {
                    transaction.rateCentralBank = it
                    transaction.calculateSumRub()
                    firebaseAPI.saveTransaction(transaction)
                    firebaseAPI.sumTaxes()
                    emit(Unit)
                }
            }*/
    }

    override fun deleteTax(request: FirebaseRequestModel): Flow<Unit> = flow {
        firebaseAPI.deleteTax(request)
        taxDao.deleteTaxes(request.toDeleteTaxDataModel())
        transactionDao.deleteTransactions(request.toDeleteTransactionDataModel())
        emit(Unit)
    }


    override fun getCBRRate(date: String, currency: String) = flow {
        cbrapi.getCurrency(date).execute().body().let {
            it?.let {
                emit(it.getCurrencyRate(currency)!!)
            }
        }
    }

    private fun SaveTransactionModel.calculateSumRub() {
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

    private fun List<TaxEntity>.toListTaxAdapterModel() =
        map { TaxAdapterModel(it.year, it.sum.toString()) }

    private fun FirebaseRequestModel.toDeleteTaxDataModel() =
        DeleteTaxDataModel(account, year)

    private fun FirebaseRequestModel.toDeleteTransactionDataModel() =
        DeleteTransactionDataModel(account, year)
}