package com.taxapprf.data.sync

import com.taxapprf.data.local.room.dao.LocalReportDao
import com.taxapprf.data.local.room.entity.LocalReportEntity
import com.taxapprf.data.remote.firebase.dao.RemoteReportDao
import com.taxapprf.data.remote.firebase.model.FirebaseReportModel
import com.taxapprf.domain.report.ReportModel

class SyncReport(
    private val localDao: LocalReportDao,
    private val remoteDao: RemoteReportDao,
    private val accountKey: String,
    private val reportKey: String,
) : SyncManager<LocalReportEntity, ReportModel, FirebaseReportModel>() {
    override fun observeLocal() = localDao.observe(accountKey, reportKey)

    override fun getLocal() = localDao.get(accountKey, reportKey)

    override fun observeRemote() = remoteDao.observe(accountKey, reportKey)

    override suspend fun saveRemote(models: Map<String, FirebaseReportModel>) {
        remoteDao.save(accountKey, models)
    }

    override fun deleteLocal(models: List<LocalReportEntity>) {
        localDao.delete(models)
    }

    override fun saveLocal(models: List<LocalReportEntity>) {
        localDao.save(models)
    }

    override fun List<ReportModel>.mapAppToRemote(): Map<String, FirebaseReportModel> {
        val map = mutableMapOf<String, FirebaseReportModel>()
        map {
            map.put(
                it.key, FirebaseReportModel(
                    key = it.key,
                    tax = it.tax,
                    size = it.size,
                    syncAt = it.syncAt
                )
            )
        }
        return map
    }

    override fun List<ReportModel>.mapAppToLocal() =
        map {
            LocalReportEntity(
                key = it.key,
                accountKey = accountKey,
                tax = it.tax,
                size = it.size,
                isSync = it.isSync,
                syncAt = it.syncAt
            )
        }

    override fun LocalReportEntity.mapLocalToApp() =
        ReportModel(key, tax, size, isSync, syncAt)
}