package com.taxapprf.data.sync

import com.taxapprf.data.local.room.dao.LocalAccountDao
import com.taxapprf.data.local.room.entity.LocalAccountEntity
import com.taxapprf.data.remote.firebase.dao.RemoteAccountDao
import com.taxapprf.domain.account.AccountModel

class SyncAccounts(
    private val localDao: LocalAccountDao,
    private val remoteDao: RemoteAccountDao,
) : SyncManager<LocalAccountEntity, AccountModel>() {
    override fun observeLocal() = localDao.observeAll()

    override fun getLocal() = localDao.getAll()

    override fun observeRemote() = remoteDao.observeAll()
    override suspend fun deleteRemote(models: List<AccountModel>) {
        remoteDao.deleteAll(models)
    }

    override suspend fun saveRemote(models: List<AccountModel>) {
        remoteDao.saveAll(models)
    }

    override fun deleteLocal(models: List<LocalAccountEntity>) {
        localDao.delete(models)
    }

    override fun saveLocal(models: List<LocalAccountEntity>) {
        localDao.save(models)
    }

    override fun AccountModel.mapAppToLocal(local: AccountModel?): LocalAccountEntity =
        LocalAccountEntity(
            id = local?.id ?: id,
            key = key,
            isActive = isActive,
            isSync = local?.isSync ?: isSync,
            isDelete = local?.isDelete ?: isDelete,
            syncAt = local?.syncAt ?: syncAt
        )

    override fun LocalAccountEntity.mapLocalToApp() =
        AccountModel(id, key, isActive, isSync, isDelete, syncAt)
}