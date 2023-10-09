package com.taxapprf.data.sync

import com.taxapprf.data.local.room.LocalDatabase.Companion.DEFAULT_ID
import com.taxapprf.data.local.room.LocalSyncDao
import com.taxapprf.data.local.room.entity.LocalAccountEntity
import com.taxapprf.data.local.room.model.sync.GetSyncResultAccountModel
import com.taxapprf.data.remote.firebase.dao.RemoteAccountDao
import com.taxapprf.data.remote.firebase.entity.FirebaseAccountEntity
import javax.inject.Inject

class SyncAccounts @Inject constructor(
    private val localDao: LocalSyncDao,
    private val remoteDao: RemoteAccountDao,
) : SyncManager<LocalAccountEntity, LocalAccountEntity, FirebaseAccountEntity>() {
    private var currentUserId: Int = 0

    suspend fun sync(userId: Int): List<GetSyncResultAccountModel> {
        currentUserId = userId
        startSync()
        return localDao.getSyncResultUserAccounts(currentUserId)
    }

    override fun getLocalInList() =
        localDao.getUserAccounts(currentUserId)

    override fun deleteLocalOutList(locals: List<LocalAccountEntity>) =
        localDao.deleteUserAccounts(locals)

    override fun saveLocalOutList(locals: List<LocalAccountEntity>) =
        localDao.saveUserAccounts(locals)

    override suspend fun getRemoteList() =
        remoteDao.getAll()

    override fun FirebaseAccountEntity.toLocalOut(localIn: LocalAccountEntity?): LocalAccountEntity? {
        val key = key ?: return null
        val isActive = localIn?.isActive ?: false
        val syncAt = syncAt ?: 0L

        return LocalAccountEntity(
            id = localIn?.id ?: DEFAULT_ID,
            userId = currentUserId,
            isActive = isActive,
            remoteKey = key,
            isSync = true,
            syncAt = syncAt
        )
    }

    override fun LocalAccountEntity.toLocalOut() =
        LocalAccountEntity(
            id = id,
            userId = userId,
            isActive = isActive,
            remoteKey = remoteKey,
            isSync = isSync,
            syncAt = syncAt
        )

    override suspend fun updateRemoteList(remoteMap: Map<String, FirebaseAccountEntity?>) =
        remoteDao.updateAll(remoteMap)

    override suspend fun LocalAccountEntity.updateRemoteKey() =
        this

    override fun LocalAccountEntity.toRemote() =
        FirebaseAccountEntity(
            syncAt = syncAt
        )
}