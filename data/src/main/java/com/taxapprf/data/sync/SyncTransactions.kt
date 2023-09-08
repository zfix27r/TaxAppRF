package com.taxapprf.data.sync

import com.taxapprf.data.local.room.dao.LocalTransactionDao
import com.taxapprf.data.local.room.entity.LocalTransactionEntity
import com.taxapprf.data.remote.firebase.dao.RemoteTransactionDao
import com.taxapprf.domain.transaction.TransactionModel

class SyncTransactions(
    private val localDao: LocalTransactionDao,
    private val remoteDao: RemoteTransactionDao,
    private val accountKey: String,
    private val reportKey: String,
) : SyncManager<LocalTransactionEntity, TransactionModel>() {
    override fun observeLocal() = localDao.observeAll(accountKey, reportKey)

    override fun getLocal() = localDao.getAll(accountKey, reportKey)

    override fun observeRemote() = remoteDao.observeAll(accountKey, reportKey)
    override suspend fun deleteRemote(models: List<TransactionModel>) {
        remoteDao.deleteAll(accountKey, reportKey, models)
    }

    override suspend fun saveRemote(models: List<TransactionModel>) {
        remoteDao.saveAll(accountKey, reportKey, models)
    }

    override fun deleteLocal(models: List<LocalTransactionEntity>) {
        localDao.delete(models)
    }

    override fun saveLocal(models: List<LocalTransactionEntity>) {
        localDao.save(models)
    }

    override fun TransactionModel.mapAppToLocal(local: TransactionModel?): LocalTransactionEntity =
        LocalTransactionEntity(
            id = local?.id ?: id,
            key = key,
            accountKey = accountKey,
            reportKey = reportKey,
            name = name,
            date = date,
            type = type,
            currency = currency,
            rateCBRF = rateCBRF,
            sum = sum,
            tax = tax,
            isSync = local?.isSync ?: isSync,
            isDelete = local?.isDelete ?: isDelete,
            syncAt = local?.syncAt ?: syncAt
        )

    override fun LocalTransactionEntity.mapLocalToApp() =
        TransactionModel(
            id,
            key,
            name,
            date,
            type,
            currency,
            rateCBRF,
            sum,
            tax,
            isSync,
            isDelete,
            syncAt
        )
}