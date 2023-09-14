package com.taxapprf.data.sync

import com.taxapprf.data.local.room.dao.LocalAccountDao
import com.taxapprf.data.local.room.entity.LocalAccountEntity
import com.taxapprf.data.remote.firebase.dao.RemoteAccountDao
import com.taxapprf.data.remote.firebase.model.FirebaseAccountModel

class SyncAccounts(
    private val localDao: LocalAccountDao,
    private val remoteDao: RemoteAccountDao,
) : SyncManager<LocalAccountEntity, FirebaseAccountModel>() {
    override fun getLocalList() =
        localDao.getAll()

    override fun deleteLocalList(locals: List<LocalAccountEntity>) =
        localDao.deleteAll(locals)

    override fun saveLocalList(locals: List<LocalAccountEntity>) =
        localDao.saveAll(locals)

    override suspend fun getRemoteList() =
        remoteDao.getAll()

    override suspend fun deleteRemoteList(remoteMap: Map<String, FirebaseAccountModel>) =
        remoteDao.deleteAll(remoteMap)

    override suspend fun saveRemoteList(remoteMap: Map<String, FirebaseAccountModel>) =
        remoteDao.saveAll(remoteMap)

    override suspend fun LocalAccountEntity.updateRemoteKey() =
        remoteDao.getKey()?.let { copy(key = it) }

    override fun LocalAccountEntity.toRemote(remote: FirebaseAccountModel?) =
        FirebaseAccountModel(
            key = key,
            isActive = isActive,
            syncAt = syncAt
        )

    override fun FirebaseAccountModel.toLocal(local: LocalAccountEntity?): LocalAccountEntity? {
        val key = key ?: return null
        val isActive = isActive ?: false
        val syncAt = syncAt ?: 0L

        return LocalAccountEntity(
            id = 0,
            key = key,
            isActive = isActive,
            isSync = true,
            isDelete = false,
            syncAt = syncAt
        )
    }
}