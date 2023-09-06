package com.taxapprf.data.sync

import com.taxapprf.data.local.room.dao.LocalTransactionDao
import com.taxapprf.data.local.room.entity.LocalTransactionEntity
import com.taxapprf.data.remote.firebase.dao.RemoteTransactionDao
import com.taxapprf.data.remote.firebase.model.FirebaseTransactionModel
import com.taxapprf.domain.transaction.TransactionModel

class SyncTransactions(
    private val localDao: LocalTransactionDao,
    private val remoteDao: RemoteTransactionDao,
    private val accountKey: String,
    private val reportKey: String,
) : SyncManager<LocalTransactionEntity, TransactionModel, FirebaseTransactionModel>() {
    override fun observeLocal() = localDao.observeAll(accountKey, reportKey)

    override fun getLocal() = localDao.getAll(accountKey, reportKey)

    override fun observeRemote() = remoteDao.observeAll(accountKey, reportKey)
    override suspend fun deleteRemote(models: Map<String, FirebaseTransactionModel?>) {
        remoteDao.deleteAll(accountKey, reportKey, models)
    }

    override suspend fun saveRemote(models: Map<String, FirebaseTransactionModel>) {
        remoteDao.saveAll(accountKey, reportKey, models)
    }

    override fun deleteLocal(models: List<LocalTransactionEntity>) {
        localDao.delete(models)
    }

    override fun saveLocal(models: List<LocalTransactionEntity>) {
        localDao.save(models)
    }

    override fun List<TransactionModel>.mapAppToRemote(): Map<String, FirebaseTransactionModel> {
        val map = mutableMapOf<String, FirebaseTransactionModel>()
        map {
            map.put(
                it.key, FirebaseTransactionModel(
                    name = it.name,
                    date = it.date,
                    type = it.type,
                    currency = it.currency,
                    rateCBR = it.rateCBR,
                    sum = it.sum,
                    tax = it.tax,
                    syncAt = it.syncAt
                )
            )
        }
        return map
    }

    override fun List<TransactionModel>.mapAppToLocal() =
        map {
            LocalTransactionEntity(
                key = it.key,
                accountKey = accountKey,
                reportKey = reportKey,
                name = it.name,
                date = it.date,
                type = it.type,
                currency = it.currency,
                rateCBR = it.rateCBR,
                sum = it.sum,
                tax = it.tax,
                isSync = it.isSync,
                isDeferredDelete = it.isDeferredDelete,
                syncAt = it.syncAt
            )
        }

    override fun LocalTransactionEntity.mapLocalToApp() =
        TransactionModel(
            key,
            name,
            date,
            type,
            currency,
            rateCBR,
            sum,
            tax,
            isSync,
            isDeferredDelete,
            syncAt
        )
}