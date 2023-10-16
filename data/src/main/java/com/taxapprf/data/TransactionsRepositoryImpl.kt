package com.taxapprf.data

import com.taxapprf.data.local.room.LocalTransactionsDao
import com.taxapprf.data.local.room.entity.LocalReportEntity
import com.taxapprf.data.local.room.model.ObserveTransactionDataModel
import com.taxapprf.domain.TransactionsRepository
import com.taxapprf.domain.currency.Currencies
import com.taxapprf.domain.transactions.ReportModel
import com.taxapprf.domain.transactions.TransactionModel
import com.taxapprf.domain.transactions.TransactionTypes
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class TransactionsRepositoryImpl @Inject constructor(
    private val localDao: LocalTransactionsDao,
) : TransactionsRepository {
    override fun observeReport(reportId: Int) =
        localDao.observeLocalReportEntity(reportId)
            .map { it?.toReportModel() }

    override fun observeTransactions(reportId: Int) =
        localDao.observeListGetTransaction(reportId)
            .map { getTransaction ->
                getTransaction.map { it.toTransactionModel() }
            }

    private fun LocalReportEntity.toReportModel() =
        ReportModel(
            id = id,
            name = remoteKey,
            sumRUB = sumRUB,
            taxRUB = taxRUB,
            size = size
        )

    private fun ObserveTransactionDataModel.toTransactionModel() =
        TransactionModel(
            id = id,
            name = name,
            date = date,
            sum = sum,
            taxRUB = taxRUB,
            type = TransactionTypes.values()[typeOrdinal],
            currency = Currencies.values()[currencyOrdinal],
            currencyRate = currencyRate
        )
}
