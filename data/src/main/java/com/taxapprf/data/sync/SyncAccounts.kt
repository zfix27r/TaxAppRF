package com.taxapprf.data.sync

import com.taxapprf.data.local.room.LocalAccountDao
import com.taxapprf.data.local.room.entity.LocalAccountEntity
import com.taxapprf.data.remote.firebase.dao.RemoteAccountDao
import com.taxapprf.data.remote.firebase.model.FirebaseAccountModel
import javax.inject.Inject

class SyncAccounts @Inject constructor(
    private val localDao: LocalAccountDao,
    private val remoteDao: RemoteAccountDao,
) : SyncManager<LocalAccountEntity, LocalAccountEntity, FirebaseAccountModel>() {
    private var currentUserId: Int? = null

    suspend fun sync(userId: Int) {
        currentUserId = userId

        startSync()
    }

    override fun getLocalList() =
        localDao.getAll()

    override fun deleteLocalList(locals: List<LocalAccountEntity>) =
        localDao.deleteAccounts(locals)

    override fun saveLocalList(locals: List<LocalAccountEntity>) =
        localDao.saveAccounts(locals)

    override suspend fun getRemoteList() =
        remoteDao.getAll()

    override fun LocalAccountEntity.toLocalOut() =
        LocalAccountEntity(
            id = id,
            userId = userId,
            remoteKey = remoteKey,
            isActive = isActive,
            syncAt = syncAt
        )

    override suspend fun updateRemoteList(remoteMap: Map<String, FirebaseAccountModel?>) =
        remoteDao.updateAll(remoteMap)

    override suspend fun LocalAccountEntity.updateRemoteKey() =
        remoteDao.getKey()?.let { copy(remoteKey = it) }

    override fun LocalAccountEntity.toRemote(remote: FirebaseAccountModel?) =
        FirebaseAccountModel(
            isActive = isActive,
            syncAt = syncAt
        )

    override fun FirebaseAccountModel.toLocal(local: LocalAccountEntity?): LocalAccountEntity? {
        val userId = currentUserId ?: return null
        val key = key ?: return null
        val isActive = isActive ?: false
        val syncAt = syncAt ?: 0L

        return LocalAccountEntity(
            id = 0,
            userId = userId,
            remoteKey = key,
            isActive = isActive,
            syncAt = syncAt
        )
    }
}