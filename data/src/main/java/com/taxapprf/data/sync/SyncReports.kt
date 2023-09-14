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
    private val reportKey: String = "",
) : SyncManager<LocalReportEntity, FirebaseReportModel, ReportModel>() {
    override fun observeLocal() = localDao.observe(accountKey, reportKey)
    override fun observeAllLocal() = localDao.observeAll(accountKey)

    override fun getLocal() = localDao.get(accountKey, reportKey)
    override fun getAllLocal() = localDao.getAll(accountKey)
    override fun getAllDeleteLocal() = localDao.getAllDelete(accountKey)

    override fun deleteLocalList(locals: List<LocalReportEntity>) {
        localDao.deleteAll(locals)
    }

    override fun saveLocalList(locals: List<LocalReportEntity>) {
        localDao.saveAll(locals)
    }

    override fun LocalReportEntity.toApp() =
        ReportModel(id, key, tax, size, isSync, isDelete, syncAt)

    override fun LocalReportEntity.toRemote(remote: FirebaseReportModel?) =
        FirebaseReportModel(key, tax, size, syncAt)

    override fun observeRemote() = remoteDao.observe(accountKey, reportKey)
    override fun getRemote() = remoteDao.observeAll(accountKey)

    override suspend fun deleteRemoteList(remotes: List<FirebaseReportModel>) {
        remoteDao.deleteAll(accountKey, remotes)
    }

    override suspend fun saveRemoteList(locales: List<LocalReportEntity>) {
        remoteDao.saveAll(accountKey, locales.map { it.toRemote() })
    }

    override fun FirebaseReportModel.toLocal(local: LocalReportEntity?): LocalReportEntity? {
        val key = key ?: return null
        val tax = this.tax ?: return null
        val size = this.size ?: return null
        val syncAt = this.syncAt ?: 0L

        return LocalReportEntity(
            id = local?.id ?: 0,
            key = key,
            accountKey = accountKey,
            tax = tax,
            size = size,
            isSync = true,
            isDelete = local?.isDelete ?: false,
            syncAt = syncAt
        )
    }
}