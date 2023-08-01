package com.taxapprf.data

import com.taxapprf.data.local.dao.TransactionDao
import com.taxapprf.data.local.dao.UserDao
import com.taxapprf.data.local.entity.TaxesEntity
import com.taxapprf.data.local.excel.ExcelParcel
import com.taxapprf.data.remote.cbrapi.CBRAPI
import com.taxapprf.domain.TaxesRepository
import com.taxapprf.domain.TransactionType
import com.taxapprf.domain.taxes.TaxesAdapterModel
import com.taxapprf.domain.transaction.SaveTransactionModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject
import kotlin.math.abs

class TaxesRepositoryImpl @Inject constructor(
    userDao: UserDao,
    private val firebaseAPI: FirebaseAPI,
    private val transactionDao: TransactionDao,
    private val cbrapi: CBRAPI,
) : TaxesRepository {
    private val accountName = runBlocking {
        withContext(Dispatchers.IO) {
            userDao.getNameActiveAccount()
        }
    }

    override fun getTaxes() = transactionDao.getTaxes(accountName)
        .map {
            it.toListTaxesAdapterModel()
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

    private fun List<TaxesEntity>.toListTaxesAdapterModel() =
        map { TaxesAdapterModel(it.year, it.sumTaxes.toString()) }
}