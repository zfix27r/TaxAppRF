package com.taxapprf.data

import com.taxapprf.data.FirebaseAPI.Companion.getAsDouble
import com.taxapprf.data.FirebaseAPI.Companion.getAsString
import com.taxapprf.data.local.dao.TaxDao
import com.taxapprf.data.local.dao.TransactionDao
import com.taxapprf.data.local.entity.TaxEntity
import com.taxapprf.data.local.entity.TransactionEntity
import com.taxapprf.data.local.excel.ExcelParcel
import com.taxapprf.data.local.model.DeleteTaxDataModel
import com.taxapprf.data.local.model.DeleteTransactionDataModel
import com.taxapprf.data.remote.cbrapi.CBRAPI
import com.taxapprf.domain.TaxRepository
import com.taxapprf.domain.TransactionType
import com.taxapprf.domain.taxes.DeleteTaxModel
import com.taxapprf.domain.taxes.TaxAdapterModel
import com.taxapprf.domain.transaction.SaveTransactionModel
import kotlinx.coroutines.flow.collectLatest
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
    override fun getTaxes(accountName: String) = taxDao.getTaxes(accountName)
        .onEmpty { getAndSaveFirebaseAccountData(accountName) }
        .map { it.toListTaxAdapterModel() }

    private suspend fun getAndSaveFirebaseAccountData(accountName: String) {
        firebaseAPI.getTaxes(accountName)
            .mapNotNull { year ->
                var sumTaxes = 0.0
                val transactions = year
                    .child(FirebaseAPI.TRANSACTIONS)
                    .children
                    .mapNotNull { transaction ->
                        year.key?.let {
                            val tr = TransactionEntity(
                                key = transaction.getAsString(FirebaseAPI.KEY_TRANSACTION_KEY),
                                account = accountName,
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
                val tax = TaxEntity(0, accountName, year.toString(), sumTaxes)
                taxDao.saveTax(tax)
            }
    }

    override fun saveTaxesFromExcel(storagePath: String) = flow {
        ExcelParcel(storagePath)
            .parse()
            .map { transaction ->
                getCBRRate(transaction.date, transaction.currency).collectLatest {
                    transaction.rateCentralBank = it
                    transaction.calculateSumRub()
                    firebaseAPI.saveTransaction(transaction)
                    firebaseAPI.sumTaxes()
                    emit(Unit)
                }
            }
    }

    override fun deleteTax(deleteTaxModel: DeleteTaxModel) = flow {
        firebaseAPI.deleteTax(deleteTaxModel)
        taxDao.deleteTaxes(deleteTaxModel.toDeleteTaxDataModel())
        transactionDao.deleteTransactions(deleteTaxModel.toDeleteTransactionDataModel())
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
        map { TaxAdapterModel(it.year, it.sumTaxes.toString()) }

    private fun DeleteTaxModel.toDeleteTaxDataModel() =
        DeleteTaxDataModel(account, year)

    private fun DeleteTaxModel.toDeleteTransactionDataModel() =
        DeleteTransactionDataModel(account, year)
}