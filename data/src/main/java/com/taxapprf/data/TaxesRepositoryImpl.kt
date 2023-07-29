package com.taxapprf.data

import com.taxapprf.data.local.excel.ExcelParcel
import com.taxapprf.data.remote.cbrapi.CBRAPI
import com.taxapprf.domain.TaxesRepository
import com.taxapprf.domain.TransactionType
import com.taxapprf.domain.transaction.SaveTransactionModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject
import kotlin.math.abs

class TaxesRepositoryImpl @Inject constructor(
    private val firebaseAPI: FirebaseAPI,
    private val cbrapi: CBRAPI,
) : TaxesRepository {
    override fun getTaxes() = flow {
        emit(firebaseAPI.getTaxes())
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
}