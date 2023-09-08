package com.taxapprf.data.sync

import com.taxapprf.data.local.room.dao.LocalReportDao
import com.taxapprf.data.local.room.entity.LocalReportEntity
import com.taxapprf.data.remote.firebase.dao.RemoteReportDao
import com.taxapprf.data.remote.firebase.model.FirebaseReportModel
import com.taxapprf.domain.report.ReportModel

class SyncReports(
    private val localDao: LocalReportDao,
    private val remoteDao: RemoteReportDao,
    private val accountKey: String,
) : SyncManager<LocalReportEntity, ReportModel, FirebaseReportModel>() {
    override fun observeLocal() = localDao.observeAll(accountKey)

    override fun getLocal() = localDao.getAll(accountKey)

    override fun observeRemote() = remoteDao.observeAll(accountKey)
    override suspend fun deleteRemote(models: Map<String, FirebaseReportModel?>) {
        remoteDao.deleteAll(accountKey, models)
    }

    override suspend fun saveRemote(models: Map<String, FirebaseReportModel>) {
        remoteDao.saveAll(accountKey, models)
    }

    override fun deleteLocal(models: List<LocalReportEntity>) {
        localDao.deleteAll(models)
    }

    override fun saveLocal(models: List<LocalReportEntity>) {
        localDao.saveAll(models)
    }

    override fun List<ReportModel>.mapAppToRemote(): Map<String, FirebaseReportModel> {
        val map = mutableMapOf<String, FirebaseReportModel>()
        map {
            map.put(
                it.key, FirebaseReportModel(
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
                id = it.id,
                key = it.key,
                accountKey = accountKey,
                tax = it.tax,
                size = it.size,
                isSync = it.isSync,
                isDelete = it.isDelete,
                syncAt = it.syncAt
            )
        }

    override fun LocalReportEntity.mapLocalToApp() =
        ReportModel(id, key, tax, size, isSync, isDelete, syncAt)
}