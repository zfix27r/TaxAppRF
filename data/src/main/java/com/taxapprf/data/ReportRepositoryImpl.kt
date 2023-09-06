package com.taxapprf.data

import com.taxapprf.data.local.room.dao.LocalReportDao
import com.taxapprf.data.local.room.entity.LocalReportEntity
import com.taxapprf.data.remote.firebase.FirebaseReportDaoImpl
import com.taxapprf.data.sync.SyncReport
import com.taxapprf.data.sync.SyncReports
import com.taxapprf.domain.ReportRepository
import com.taxapprf.domain.report.DeleteWithTransactionsModel
import com.taxapprf.domain.report.ObserveReportModel
import com.taxapprf.domain.report.ObserveReportsModel
import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.report.SaveReportModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepositoryImpl @Inject constructor(
    private val networkManager: NetworkManager,
    private val localReportDao: LocalReportDao,
    private val remoteReportDao: FirebaseReportDaoImpl,
) : ReportRepository {
    override fun observe(observeReportModel: ObserveReportModel) =
        SyncReport(
            localReportDao,
            remoteReportDao,
            observeReportModel.accountKey,
            observeReportModel.reportKey
        ).observe()

    override fun observeAll(observeReportsModel: ObserveReportsModel) =
        SyncReports(
            localReportDao,
            remoteReportDao,
            observeReportsModel.accountKey
        ).observe()

    override fun get(accountKey: String, reportKey: String) =
        localReportDao.get(accountKey, reportKey).toReportModel()

    override fun deleteWithTransactions(deleteReportModel: DeleteWithTransactionsModel) =
        deleteWithTransactions(deleteReportModel.accountKey, deleteReportModel.reportKey)

    override fun deleteWithTransactions(accountKey: String, reportKey: String) = flow {
        if (networkManager.available) {
            remoteReportDao.delete(accountKey, reportKey)
            deleteTransaction(accountKey, reportKey)
            localReportDao.delete(accountKey, reportKey)
        } else {
            localReportDao.deleteDeferred(accountKey, reportKey)
            deleteDeferredTransaction(accountKey, reportKey)
        }
        emit(Unit)
    }

    override suspend fun deleteTransaction(accountKey: String, reportKey: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteDeferredTransaction(accountKey: String, reportKey: String) {
        TODO("Not yet implemented")
    }

    override fun save(saveReportModel: SaveReportModel) = flow {
        if (networkManager.available) remoteReportDao.save(saveReportModel)
        else localReportDao.save(saveReportModel.toDeferredLocalReportEntity())

        emit(Unit)
    }

    private fun LocalReportEntity?.toReportModel() =
        this?.let { ReportModel(key, tax, size, isSync, isDeferredDelete, syncAt) }

    private fun SaveReportModel.toDeferredLocalReportEntity() =
        LocalReportEntity(
            reportKey, accountKey, tax, size, false,
            isDeferredDelete = false,
            syncAt = getTime()
        )
}