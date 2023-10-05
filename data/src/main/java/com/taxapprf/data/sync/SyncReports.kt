package com.taxapprf.data.sync

import com.taxapprf.data.local.room.LocalReportDao
import com.taxapprf.data.local.room.entity.LocalReportEntity
import com.taxapprf.data.remote.firebase.dao.RemoteReportDao
import com.taxapprf.data.remote.firebase.model.FirebaseReportModel

class SyncReports(
    private val localDao: LocalReportDao,
    private val remoteDao: RemoteReportDao,
    private val accountId: Int,
    private val accountKey: String,
) : SyncManager<LocalReportEntity, LocalReportEntity, FirebaseReportModel>() {
    override fun getLocalList() =
        localDao.getAll(accountId)

    override fun deleteLocalList(locals: List<LocalReportEntity>) =
        localDao.deleteReports(locals)

    override fun saveLocalList(locals: List<LocalReportEntity>) =
        localDao.saveReports(locals)

    override suspend fun getRemoteList() =
        remoteDao.getAll(accountKey)

    override fun LocalReportEntity.toLocalOut() =
        LocalReportEntity(
            id = id,
            accountId = accountId,
            tax = tax,
            size = size,
            remoteKey = remoteKey,
            syncAt = syncAt
        )

    override fun LocalReportEntity.toRemote(remote: FirebaseReportModel?) =
        FirebaseReportModel(tax, size, syncAt)

    override fun FirebaseReportModel.toLocal(local: LocalReportEntity?): LocalReportEntity? {
        val key = key ?: return null
        val tax = this.tax ?: return null
        val size = this.size ?: return null
        val syncAt = this.syncAt ?: 0L

        return LocalReportEntity(
            id = local?.id ?: 0,
            accountId = accountId,
            tax = tax,
            size = size,
            remoteKey = key,
            syncAt = syncAt
        )
    }

    override suspend fun LocalReportEntity.updateRemoteKey() =
        remoteDao.getKey(accountKey)?.let { copy(remoteKey = it) }

    override suspend fun updateRemoteList(remoteMap: Map<String, FirebaseReportModel?>) =
        remoteDao.updateAll(accountKey, remoteMap)
}