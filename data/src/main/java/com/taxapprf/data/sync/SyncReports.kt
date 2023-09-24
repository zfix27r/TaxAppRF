package com.taxapprf.data.sync

import com.taxapprf.data.local.room.dao.LocalReportDao
import com.taxapprf.data.local.room.entity.LocalReportEntity
import com.taxapprf.data.remote.firebase.dao.RemoteReportDao
import com.taxapprf.data.remote.firebase.model.FirebaseReportModel

class SyncReports(
    private val localDao: LocalReportDao,
    private val remoteDao: RemoteReportDao,
    private val accountKey: String,
) : SyncManager<LocalReportEntity, FirebaseReportModel>() {
    override fun getLocalList() =
        localDao.getAll(accountKey)

    override fun deleteLocalList(locals: List<LocalReportEntity>) =
        localDao.deleteAll(locals)

    override fun saveLocalList(locals: List<LocalReportEntity>) =
        localDao.saveAll(locals)

    override suspend fun getRemoteList() =
        remoteDao.getAll(accountKey)

    override fun LocalReportEntity.toRemote(remote: FirebaseReportModel?) =
        FirebaseReportModel(tax, size, syncAt)

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

    override suspend fun LocalReportEntity.updateRemoteKey() =
        remoteDao.getKey(accountKey)?.let { copy(key = it) }

    override suspend fun updateRemoteList(remoteMap: Map<String, FirebaseReportModel?>) =
        remoteDao.updateAll(accountKey, remoteMap)
}