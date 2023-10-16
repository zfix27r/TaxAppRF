package com.taxapprf.data

import com.taxapprf.data.local.room.LocalTaxDao
import com.taxapprf.data.local.room.model.tax.TransactionSumRUBAndTaxRUBDataModel
import com.taxapprf.domain.TaxRepository
import com.taxapprf.domain.transactions.TransactionTypes
import javax.inject.Inject


class TaxRepositoryImpl @Inject constructor(
    private val currencyRepository: CurrencyRepositoryImpl,
    private val localTaxDao: LocalTaxDao,
) : TaxRepository {
    override suspend fun updateAllEmptySumRUBAndTaxRUB() {
        val reports = mutableMapOf<Int, Double>()
        val transactions = localTaxDao.getTransactionSumRUBAndTaxRUBDataModels()

        transactions.forEach { transaction ->
            calculateSumRUB(transaction)?.let { sumRUB ->
                transaction.sumRUB = sumRUB

                if (!reports.containsKey(transaction.reportId))
                    localTaxDao.getReportSumRUB(transaction.reportId)?.let {
                        reports[transaction.reportId] = it
                    }
            }
        }

        if (reports.isNotEmpty())
            localTaxDao.updateTransactionSumRUBAndTaxRUBDataModels(transactions)
        else return

        reports.forEach { report ->
            localTaxDao.updateReportSumRUB(report.key)
        }

        reports.forEach { report ->
            localTaxDao.getReportSumRUB(report.key)?.let { newReportSumRUB ->
                if (isSumRUBMoreThenLuxury(newReportSumRUB, report.value) ||
                    isSumRUBLessThenLuxury(newReportSumRUB, report.value)
                ) {
                    val reportTransactions =
                        localTaxDao.getTransactionSumRUBAndTaxRUBDataModels(report.key)
                    reportTransactions.forEach {
                        calculateTaxRUB(newReportSumRUB, it)?.let { taxRUB ->
                            it.taxRUB = taxRUB
                        }
                    }
                    localTaxDao.updateTransactionSumRUBAndTaxRUBDataModels(reportTransactions)
                    reports[report.key] = newReportSumRUB
                }
            }
        }


        transactions.forEach { transaction ->
            if (reports.containsKey(transaction.reportId)) {
                calculateTaxRUB(reports[transaction.reportId]!!, transaction)?.let { taxRUB ->
                    transaction.taxRUB = taxRUB
                }
            }
        }

        localTaxDao.updateTransactionSumRUBAndTaxRUBDataModels(transactions)

        reports.forEach { report ->
            localTaxDao.updateReportTaxRUB(report.key)
        }
    }

    private fun isSumRUBMoreThenLuxury(newSumRUB: Double, oldSumRUB: Double) =
        newSumRUB > SUM_RUB_LUXURY && oldSumRUB <= SUM_RUB_LUXURY

    private fun isSumRUBLessThenLuxury(newSumRUB: Double, oldSumRUB: Double) =
        newSumRUB <= SUM_RUB_LUXURY && oldSumRUB > SUM_RUB_LUXURY

    private suspend fun calculateSumRUB(
        transactionSumRUBAndTaxRUBDataModel: TransactionSumRUBAndTaxRUBDataModel
    ) =
        try {
            currencyRepository.getCurrencyRate(
                transactionSumRUBAndTaxRUBDataModel.currencyOrdinal,
                transactionSumRUBAndTaxRUBDataModel.date
            )?.let { currencyRate ->
                if (currencyRate.isRateNotFoundOnCBR()) null
                else transactionSumRUBAndTaxRUBDataModel.sum * currencyRate
            }
        } catch (_: Exception) {
            null
        }

    private fun Double.isRateNotFoundOnCBR() = this < 0.0

    private fun calculateTaxRUB(
        reportSumRUB: Double,
        transactionSumRUBAndTaxRUBDataModel: TransactionSumRUBAndTaxRUBDataModel
    ) =
        try {
            val k = TransactionTypes.values()[transactionSumRUBAndTaxRUBDataModel.typeOrdinal].k
            if (k != 0) {
                val taxRF =
                    if (reportSumRUB > SUM_RUB_LUXURY) RUB_TAX_LUXURY else RUB_TAX_BASE
                transactionSumRUBAndTaxRUBDataModel.sumRUB!! * taxRF * k
            } else 0.0
        } catch (_: Exception) {
            null
        }

    companion object {
        const val RUB_TAX_BASE = 0.13
        const val RUB_TAX_LUXURY = 0.15
        const val SUM_RUB_LUXURY = 5_000_000.0
    }
}