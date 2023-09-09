package com.taxapprf.data.sync

import com.taxapprf.data.local.room.dao.LocalAccountDao
import com.taxapprf.data.local.room.entity.LocalAccountEntity
import com.taxapprf.data.remote.firebase.dao.RemoteAccountDao
import com.taxapprf.data.remote.firebase.model.FirebaseAccountModel
import com.taxapprf.domain.account.AccountModel

class SyncAccounts(
    private val localDao: LocalAccountDao,
    private val remoteDao: RemoteAccountDao,
) : SyncManager<LocalAccountEntity, FirebaseAccountModel, AccountModel>() {
    override fun observeLocal() = TODO()
    override fun observeAllLocal() = localDao.observeAll()

    override fun getLocal() = TODO()
    override fun getAllLocal() = localDao.getAll()

    override fun deleteAllLocal(locals: List<LocalAccountEntity>) {
        localDao.deleteAll(locals)
    }

    override fun saveAllLocal(locals: List<LocalAccountEntity>) {
        localDao.saveAll(locals)
    }

    override fun LocalAccountEntity.toApp(): AccountModel =
        AccountModel(id, key, isActive, isSync, isDelete, syncAt)

    override fun LocalAccountEntity.toRemote(remote: FirebaseAccountModel?) =
        FirebaseAccountModel(
            key = key,
            isActive = isActive,
            syncAt = syncAt
        )

    override fun observeRemote() = TODO()
    override fun observeAllRemote() = remoteDao.observeAll()

    override suspend fun deleteAllRemote(remotes: List<FirebaseAccountModel>) {
        remoteDao.deleteAll(remotes)
    }

    override suspend fun saveAllRemote(locales: List<LocalAccountEntity>) {
        remoteDao.saveAll(locales.map { it.toRemote() })
    }

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