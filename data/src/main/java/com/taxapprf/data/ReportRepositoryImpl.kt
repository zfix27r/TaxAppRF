package com.taxapprf.data

import com.taxapprf.data.local.room.dao.LocalReportDao
import com.taxapprf.data.local.room.dao.LocalTransactionDao
import com.taxapprf.data.local.room.model.LocalDeleteReportModel
import com.taxapprf.data.local.room.model.LocalDeleteTransactionModel
import com.taxapprf.data.remote.firebase.FirebaseReportDaoImpl
import com.taxapprf.data.sync.SyncReport
import com.taxapprf.data.sync.SyncReports
import com.taxapprf.domain.ReportRepository
import com.taxapprf.domain.report.DeleteReportModel
import com.taxapprf.domain.report.ObserveReportModel
import com.taxapprf.domain.report.ObserveReportsModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val localReportDao: LocalReportDao,
    private val remoteReportDao: FirebaseReportDaoImpl,
    private val localTransactionDao: LocalTransactionDao,
) : ReportRepository {
    override fun observeReport(observeReportModel: ObserveReportModel) =
        SyncReport(
            localReportDao,
            remoteReportDao,
            observeReportModel.accountKey,
            observeReportModel.reportKey
        ).observe()

    override fun observeReports(observeReportsModel: ObserveReportsModel) =
        SyncReports(
            localReportDao,
            remoteReportDao,
            observeReportsModel.accountKey
        ).observe()

    override fun deleteReport(deleteReportModel: DeleteReportModel) = flow {
        with(deleteReportModel) {
            remoteReportDao.delete(accountKey, reportKey)
            localReportDao.delete(deleteReportModel.toLocalDeleteReportModel())
            localTransactionDao.delete(deleteReportModel.toLocalDeleteTransactionModel())
        }
        emit(Unit)
    }

    private fun DeleteReportModel.toLocalDeleteReportModel() =
        LocalDeleteReportModel(accountKey, reportKey)

    private fun DeleteReportModel.toLocalDeleteTransactionModel() =
        LocalDeleteTransactionModel(accountKey, reportKey)
}