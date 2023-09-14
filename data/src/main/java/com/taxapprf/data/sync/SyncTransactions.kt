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
) : SyncManager<LocalTransactionEntity, FirebaseTransactionModel, TransactionModel>() {
    override fun observeLocal() = TODO()
    override fun observeAllLocal() = localDao.observeAll(accountKey, reportKey)

    override fun getLocal() = TODO()
    override fun getAllLocal() = localDao.getAll(accountKey, reportKey)
    override fun getAllDeleteLocal() = localDao.getAllDelete(accountKey)

    override fun deleteLocalList(locals: List<LocalTransactionEntity>) {
        localDao.deleteAll(locals)
    }

    override fun saveLocalList(locals: List<LocalTransactionEntity>) {
        localDao.saveAll(locals)
    }

    override fun LocalTransactionEntity.toApp() =
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

    override fun LocalTransactionEntity.toRemote(remote: FirebaseTransactionModel?): FirebaseTransactionModel {
        val firebaseTransactionModel =
            FirebaseTransactionModel(name, date, type, currency, rateCBRF, sum, tax, syncAt)
        firebaseTransactionModel.key = key
        return firebaseTransactionModel
    }

    override fun observeRemote() = TODO()
    override fun getRemote() = remoteDao.observeAll(accountKey, reportKey)

    override suspend fun deleteRemoteList(remotes: List<FirebaseTransactionModel>) {
        remoteDao.deleteAll(accountKey, reportKey, remotes)
    }

    override suspend fun saveRemoteList(locales: List<LocalTransactionEntity>) {
        val updateRemotes = mutableMapOf<String, FirebaseTransactionModel>()
        locales.map { transition ->
            if (transition.key.isEmptyKey())
                remoteDao.save(accountKey, reportKey, transition.key, transition.toRemote())
            else {
                remoteDao.getKey(accountKey, reportKey)?.let { key ->
                    localDao.save(transition.copy(key = key))
                    remoteDao.save(accountKey, reportKey, key, transition.toRemote())
                }
            }
        }
    }

    override fun FirebaseTransactionModel.toLocal(local: LocalTransactionEntity?): LocalTransactionEntity? {
        val transactionKey = key ?: return null
        val name = name
        val date = date ?: return null
        val type = type ?: return null
        val currency = currency ?: return null
        val rateCBR = rateCBR ?: 0.0
        val sum = sum ?: return null
        val tax = tax ?: 0.0
        val syncAt = syncAt ?: 0

        return LocalTransactionEntity(
            id = local?.id ?: 0,
            accountKey = accountKey,
            reportKey = reportKey,
            key = transactionKey,
            name,
            date,
            type,
            currency,
            rateCBR,
            sum,
            tax,
            isSync = true,
            isDelete = false,
            syncAt = syncAt
        )
    }
}