package com.taxapprf.data

import com.taxapprf.data.local.room.LocalReportsDao
import com.taxapprf.data.local.room.entity.LocalReportEntity
import com.taxapprf.domain.ReportsRepository
import com.taxapprf.domain.transactions.ReportModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ReportsRepositoryImpl @Inject constructor(
    private val localReportsDao: LocalReportsDao,
) : ReportsRepository {
    override fun observeAccountReports(accountId: Int) =
        localReportsDao.observeAccountReports(accountId)
            .map { reports -> reports.map { it.toReportModel() } }

    private fun LocalReportEntity.toReportModel() =
        ReportModel(id, remoteKey, tax, size)
}