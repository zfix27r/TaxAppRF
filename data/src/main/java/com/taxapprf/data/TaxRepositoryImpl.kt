package com.taxapprf.data

import com.taxapprf.data.local.room.LocalTaxDao
import com.taxapprf.data.local.room.model.tax.TransactionSumRUBDataModel
import com.taxapprf.data.local.room.model.tax.TransactionTaxRUBDataModel
import com.taxapprf.domain.TaxRepository
import com.taxapprf.domain.transactions.TransactionTypes
import javax.inject.Inject


class TaxRepositoryImpl @Inject constructor(
    private val currencyRepository: CurrencyRepositoryImpl,
    private val localTaxDao: LocalTaxDao,
) : TaxRepository {
    override suspend fun updateAllEmptySumRUB() {
        val updatedReports = mutableSetOf<Int>()

        val transactionSumRUBModels = localTaxDao.getTransactionSumRUBModels()
        transactionSumRUBModels.forEach { transactionSumRUBModel ->
            calculateSumRUB(transactionSumRUBModel)?.let { sumRUB ->
                transactionSumRUBModel.sumRUB = sumRUB
                transactionSumRUBModel.taxRUB = null
                updatedReports.add(transactionSumRUBModel.reportId)
            }
        }

        if (updatedReports.isNotEmpty())
            localTaxDao.updateAllUpdatedSumRUB(updatedReports, transactionSumRUBModels)
    }

    override fun updateAllEmptyTaxRUB() {
        val updatedReports = mutableMapOf<Int, Double>()

        val transactionTaxRUBModels = localTaxDao.getTransactionTaxRUBModels()
        transactionTaxRUBModels.forEach { transactionTaxRUBModel ->
            updatedReports[transactionTaxRUBModel.reportId]
                ?: localTaxDao.getReportSumRUB(transactionTaxRUBModel.reportId)
                    ?.let { reportSumRUB ->
                        calculateTaxRUB(reportSumRUB, transactionTaxRUBModel)?.let { taxRUB ->
                            transactionTaxRUBModel.taxRUB = taxRUB
                            updatedReports[transactionTaxRUBModel.reportId] = reportSumRUB
                        }
                    }
        }

        if (updatedReports.isNotEmpty())
            localTaxDao.updateAllUpdatedTaxRUB(updatedReports, transactionTaxRUBModels)
    }

    private suspend fun calculateSumRUB(transactionSumRUBModel: TransactionSumRUBDataModel) =
        try {
            currencyRepository.getCurrencyRate(
                transactionSumRUBModel.currencyOrdinal,
                transactionSumRUBModel.date
            )?.let { currencyRate ->
                if (currencyRate.isRateNotFoundOnCBR()) null
                else transactionSumRUBModel.sum * currencyRate
            }
        } catch (_: Exception) {
            null
        }

    private fun Double.isRateNotFoundOnCBR() = this < 0.0

    private fun calculateTaxRUB(
        reportSumRUB: Double,
        transactionTaxRUBModel: TransactionTaxRUBDataModel
    ) =
        try {
            val k = TransactionTypes.values()[transactionTaxRUBModel.typeOrdinal].k
            if (k != 0) {
                val taxRF =
                    if (reportSumRUB > RUB_SUM_LUXURY) RUB_TAX_LUXURY else RUB_TAX_BASE
                transactionTaxRUBModel.sumRUB!! * taxRF * k
            } else 0.0
        } catch (_: Exception) {
            null
        }

    companion object {
        const val RUB_TAX_BASE = 0.13
        const val RUB_TAX_LUXURY = 0.15
        const val RUB_SUM_LUXURY = 5_000_000
    }
}