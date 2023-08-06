package com.taxapprf.data

import com.taxapprf.data.local.room.entity.TaxEntity
import com.taxapprf.data.local.room.model.DeleteTaxDataModel
import com.taxapprf.data.local.room.model.DeleteTransactionDataModel
import com.taxapprf.data.remote.cbrapi.CBRAPI
import com.taxapprf.data.remote.firebase.FirebaseReportDaoImpl
import com.taxapprf.data.remote.firebase.model.FirebaseReportModel
import com.taxapprf.domain.FirebaseRequestModel
import com.taxapprf.domain.ReportRepository
import com.taxapprf.domain.TransactionType
import com.taxapprf.domain.taxes.ReportAdapterModel
import com.taxapprf.domain.transaction.SaveTransactionModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject
import kotlin.math.abs

class TaxRepositoryImpl @Inject constructor(
    private val firebaseReportDao: FirebaseReportDaoImpl,
    private val cbrapi: CBRAPI,
) : ReportRepository {
    override fun getReport(accountKey: String): Flow<List<ReportAdapterModel>> = flow {
        emit(
            firebaseReportDao.getReports(accountKey)
                .map { it.toReportAdapterModel() }
        )
    }

    private fun FirebaseReportModel.toReportAdapterModel() =
        ReportAdapterModel(
            name = name ?: "",
            sum = tax ?: ""
        )

    private suspend fun getAndSaveFirebaseAccountData(request: FirebaseRequestModel) {
        /*        firebaseAPI.getTaxes(request)
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
                    }*/
    }

    override fun saveReportFromExcel(storagePath: String) = flow<Unit> {
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

    override fun deleteReport(request: FirebaseRequestModel): Flow<Unit> = flow {
        firebaseAPI.deleteTax(request)
        taxDao.deleteTaxes(request.toDeleteTaxDataModel())
        transactionDao.deleteTransactions(request.toDeleteTransactionDataModel())
        emit(Unit)
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
        map { ReportAdapterModel(it.year, it.sum.toString()) }

    private fun FirebaseRequestModel.toDeleteTaxDataModel() =
        DeleteTaxDataModel(account, year)

    private fun FirebaseRequestModel.toDeleteTransactionDataModel() =
        DeleteTransactionDataModel(account, year)
}