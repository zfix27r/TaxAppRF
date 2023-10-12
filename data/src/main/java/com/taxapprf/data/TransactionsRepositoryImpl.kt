package com.taxapprf.data

import com.taxapprf.data.local.room.LocalTransactionsDao
import com.taxapprf.data.local.room.entity.LocalReportEntity
import com.taxapprf.domain.TransactionsRepository
import com.taxapprf.domain.transactions.ReportModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class TransactionsRepositoryImpl @Inject constructor(
    private val localDao: LocalTransactionsDao,
) : TransactionsRepository {
    override fun observeReport(reportId: Int) =
        localDao.observeLocalReportEntity(reportId)
            .map { it?.toReportModel() }

    private fun LocalReportEntity.toReportModel() =
        ReportModel(
            id = id,
            name = remoteKey,
            tax = tax,
            size = size
        )
}
