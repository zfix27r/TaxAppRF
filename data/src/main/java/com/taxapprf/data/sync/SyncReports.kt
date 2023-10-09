package com.taxapprf.data.sync

import com.taxapprf.data.ReportRepositoryImpl.Companion.DEFAULT_TAX
import com.taxapprf.data.getEpochTime
import com.taxapprf.data.local.room.LocalDatabase.Companion.DEFAULT_ID
import com.taxapprf.data.local.room.LocalSyncDao
import com.taxapprf.data.local.room.entity.LocalReportEntity
import com.taxapprf.data.local.room.entity.LocalReportEntity.Companion.DEFAULT_SIZE
import com.taxapprf.data.local.room.model.sync.GetSyncResultAccountModel
import com.taxapprf.data.local.room.model.sync.GetSyncResultReportModel
import com.taxapprf.data.remote.firebase.dao.RemoteReportDao
import com.taxapprf.data.remote.firebase.entity.FirebaseReportEntity

class SyncReports(
    private val localDao: LocalSyncDao,
    private val remoteDao: RemoteReportDao,
) : SyncManager<LocalReportEntity, LocalReportEntity, FirebaseReportEntity>() {
    private var currentAccountId: Int = 0
    private var currentAccountKey: String = ""

    suspend fun sync(getSyncResultAccountModel: GetSyncResultAccountModel): List<GetSyncResultReportModel> {
        getSyncResultAccountModel.accountKey?.let {
            currentAccountId = getSyncResultAccountModel.accountId
            currentAccountKey = getSyncResultAccountModel.accountKey

            startSync()
        }

        return localDao.getSyncResultAccountReports(currentAccountId)
    }

    override fun getLocalInList() =
        localDao.getAccountReports(currentAccountId)

    override fun deleteLocalOutList(locals: List<LocalReportEntity>) =
        localDao.deleteAccountReports(locals)

    override fun saveLocalOutList(locals: List<LocalReportEntity>) =
        localDao.saveAccountReports(locals)

    override suspend fun getRemoteList() =
        remoteDao.getAll(currentAccountKey)

    override fun FirebaseReportEntity.toLocalOut(localIn: LocalReportEntity?): LocalReportEntity? {
        val key = key ?: return null
        val syncAt = this.syncAt ?: getEpochTime()

        return LocalReportEntity(
            id = localIn?.id ?: DEFAULT_ID,
            accountId = currentAccountId,
            tax = localIn?.tax ?: DEFAULT_TAX,
            size = localIn?.size ?: DEFAULT_SIZE,
            remoteKey = key,
            isSync = true,
            syncAt = syncAt
        )
    }

    override fun LocalReportEntity.toLocalOut() =
        LocalReportEntity(
            id = id,
            accountId = accountId,
            tax = tax,
            size = size,
            remoteKey = remoteKey,
            isSync = isSync,
            syncAt = syncAt
        )

    override fun LocalReportEntity.toRemote(): FirebaseReportEntity =
        FirebaseReportEntity(syncAt)

    override suspend fun LocalReportEntity.updateRemoteKey() =
        this

    override suspend fun updateRemoteList(remoteMap: Map<String, FirebaseReportEntity?>) =
        remoteDao.updateAll(currentAccountKey, remoteMap)
}