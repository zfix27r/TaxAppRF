package com.taxapprf.data.sync

import com.taxapprf.data.local.room.dao.LocalReportDao
import com.taxapprf.data.local.room.entity.LocalReportEntity
import com.taxapprf.data.remote.firebase.dao.RemoteReportDao
import com.taxapprf.domain.report.ReportModel
import kotlinx.coroutines.flow.map

class SyncReport(
    private val localDao: LocalReportDao,
    private val remoteDao: RemoteReportDao,
    private val accountKey: String,
    private val reportKey: String,
) : SyncManager<LocalReportEntity, ReportModel>() {
    override fun observeLocal() = localDao.observe(accountKey, reportKey).map {
        it?.let { listOf(it) } ?: run { emptyList() }
    }

    override fun getLocal() = localDao.get(accountKey, reportKey)
        ?.let { listOf(it) } ?: run { emptyList() }

    override fun observeRemote() = remoteDao.observe(accountKey, reportKey)
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