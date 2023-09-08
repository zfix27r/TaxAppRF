package com.taxapprf.data.sync

import com.taxapprf.data.local.room.dao.LocalAccountDao
import com.taxapprf.data.local.room.entity.LocalAccountEntity
import com.taxapprf.data.remote.firebase.dao.RemoteAccountDao
import com.taxapprf.data.remote.firebase.model.FirebaseAccountModel
import com.taxapprf.domain.account.AccountModel

class SyncAccounts(
    private val localDao: LocalAccountDao,
    private val remoteDao: RemoteAccountDao,
) : SyncManager<LocalAccountEntity, AccountModel, FirebaseAccountModel>() {
    override fun observeLocal() = localDao.observeAll()

    override fun getLocal() = localDao.getAll()

    override fun observeRemote() = remoteDao.observeAll()
    override suspend fun deleteRemote(models: Map<String, FirebaseAccountModel?>) {
        remoteDao.deleteAll(models)
    }

    override suspend fun saveRemote(models: Map<String, FirebaseAccountModel>) {
        remoteDao.saveAll(models)
    }

    override fun deleteLocal(models: List<LocalAccountEntity>) {
        localDao.delete(models)
    }

    override fun saveLocal(models: List<LocalAccountEntity>) {
        localDao.save(models)
    }

    override fun List<AccountModel>.mapAppToRemote(): Map<String, FirebaseAccountModel> {
        val map = mutableMapOf<String, FirebaseAccountModel>()
        map {
            map.put(
                it.key, FirebaseAccountModel(
                    name = it.key,
                    active = it.isActive,
                    syncAt = it.syncAt
                )
            )
        }
        return map
    }

    override fun List<AccountModel>.mapAppToLocal() =
        map {
            LocalAccountEntity(
                id = it.id,
                key = it.key,
                isActive = it.isActive,
                isDelete = it.isDelete,
                isSync = it.isSync,
                syncAt = it.syncAt
            )
        }

    override fun LocalAccountEntity.mapLocalToApp() =
        AccountModel(id, key, isActive, isSync, isDelete, syncAt)
}