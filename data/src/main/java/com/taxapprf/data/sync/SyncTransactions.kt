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
) : SyncManager<LocalTransactionEntity, FirebaseTransactionModel>() {
    override fun LocalTransactionEntity.toRemote(remote: FirebaseTransactionModel?): FirebaseTransactionModel {
        val firebaseTransactionModel =
            FirebaseTransactionModel(name, date, type, currency, rateCBRF, sum, tax, syncAt)
        firebaseTransactionModel.key = key
        return firebaseTransactionModel
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

    override fun getLocalList() =
        localDao.getAll(accountKey, reportKey)

    override fun deleteLocalList(locals: List<LocalTransactionEntity>) =
        localDao.deleteAll(locals)

    override fun saveLocalList(locals: List<LocalTransactionEntity>) =
        localDao.saveAll(locals)

    override suspend fun getRemoteList() =
        remoteDao.getAll(accountKey, reportKey)

    override suspend fun LocalTransactionEntity.updateRemoteKey() =
        remoteDao.getKey(accountKey, reportKey)?.let { copy(key = it) }

    override suspend fun updateRemoteList(remoteMap: Map<String, FirebaseTransactionModel?>) =
        remoteDao.updateAll(accountKey, reportKey, remoteMap)
}