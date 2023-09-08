package com.taxapprf.data.sync

import com.taxapprf.data.local.room.dao.LocalReportDao
import com.taxapprf.data.local.room.entity.LocalReportEntity
import com.taxapprf.data.remote.firebase.dao.RemoteReportDao
import com.taxapprf.domain.report.ReportModel

class SyncReports(
    private val localDao: LocalReportDao,
    private val remoteDao: RemoteReportDao,
    private val accountKey: String,
) : SyncManager<LocalReportEntity, ReportModel>() {
    override fun observeLocal() = localDao.observeAll(accountKey)

    override fun getLocal() = localDao.getAll(accountKey)

    override fun observeRemote() = remoteDao.observeAll(accountKey)
    override suspend fun deleteRemote(models: List<ReportModel>) {
        remoteDao.deleteAll(accountKey, models)
    }

    override suspend fun saveRemote(models: List<ReportModel>) {
        remoteDao.saveAll(accountKey, models)
    }

    override fun deleteLocal(models: List<LocalReportEntity>) {
        localDao.deleteAll(models)
    }

    override fun saveLocal(models: List<LocalReportEntity>) {
        localDao.saveAll(models)
    }

    override fun ReportModel.mapAppToLocal(local: ReportModel?): LocalReportEntity =
        LocalReportEntity(
            id = local?.id ?: id,
            key = key,
            accountKey = accountKey,
            tax = tax,
            size = size,
            isSync = local?.isSync ?: isSync,
            isDelete = local?.isDelete ?: isDelete,
            syncAt = local?.syncAt ?: syncAt
        )

    override fun LocalReportEntity.mapLocalToApp() =
        ReportModel(id, key, tax, size, isSync, isDelete, syncAt)
}