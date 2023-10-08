package com.taxapprf.data.sync

import com.taxapprf.data.local.room.LocalDatabase.Companion.DEFAULT_ID
import com.taxapprf.data.local.room.LocalSyncDao
import com.taxapprf.data.local.room.entity.LocalTransactionEntity
import com.taxapprf.data.local.room.model.sync.GetSyncResultReportModel
import com.taxapprf.data.local.room.model.sync.GetSyncTransactionModel
import com.taxapprf.data.remote.firebase.dao.RemoteTransactionDao
import com.taxapprf.data.remote.firebase.model.FirebaseTransactionModel
import com.taxapprf.domain.cbr.Currencies
import com.taxapprf.domain.transaction.TransactionTypes

class SyncTransactions(
    private val localDao: LocalSyncDao,
    private val remoteDao: RemoteTransactionDao,
) : SyncManager<GetSyncTransactionModel, LocalTransactionEntity, FirebaseTransactionModel>() {
    private var currentReportId: Int = 0

    private var currentAccountKey: String = ""
    private var currentReportKey: String = ""

    suspend fun sync(getSyncResultReportModel: GetSyncResultReportModel) {
        currentReportId = getSyncResultReportModel.reportId
        currentAccountKey = getSyncResultReportModel.accountKey
        currentReportKey = getSyncResultReportModel.reportKey

        startSync()
    }

    override fun GetSyncTransactionModel.toRemote(): FirebaseTransactionModel {
        val firebaseTransactionModel =
            FirebaseTransactionModel(
                name = name,
                date = date,
                type = TransactionTypes.values()[typeOrdinal].name,
                currency = Currencies.values()[currencyOrdinal].name,
                rateCBR = currencyRate,
                sum = sum,
                tax = tax,
                syncAt = syncAt
            )
        firebaseTransactionModel.key = remoteKey
        return firebaseTransactionModel
    }

    override fun FirebaseTransactionModel.toLocalOut(localIn: GetSyncTransactionModel?): LocalTransactionEntity? {
        val transactionKey = key ?: return null
        val date = date ?: return null
        val typeOrdinal = type?.findTransactionType()?.ordinal ?: return null
        val currencyOrdinal = currency?.findCurrency()?.ordinal ?: return null
        val sum = sum ?: return null
        val syncAt = syncAt ?: 0

        return LocalTransactionEntity(
            id = localIn?.transactionId ?: DEFAULT_ID,
            reportId = currentReportId,
            typeOrdinal = typeOrdinal,
            currencyOrdinal = currencyOrdinal,
            name = name,
            date = date,
            sum = sum,
            tax = tax,
            remoteKey = transactionKey,
            isSync = true,
            syncAt = syncAt
        )
    }

    override fun getLocalInList() =
        localDao.getTransactions(currentReportId)

    override fun deleteLocalOutList(locals: List<LocalTransactionEntity>): Int =
        localDao.deleteTransactions(locals)

    override fun saveLocalOutList(locals: List<LocalTransactionEntity>): List<Long> =
        localDao.saveTransactions(locals)

    override suspend fun getRemoteList() =
        remoteDao.getAll(currentAccountKey, currentReportKey)

    override fun GetSyncTransactionModel.toLocalOut() =
        LocalTransactionEntity(
            id = transactionId,
            reportId = currentReportId,
            typeOrdinal = typeOrdinal,
            currencyOrdinal = currencyOrdinal,
            name = name,
            date = date,
            sum = sum,
            tax = tax,
            remoteKey = remoteKey,
            isSync = isSync,
            syncAt = syncAt
        )

    override suspend fun GetSyncTransactionModel.updateRemoteKey() =
        remoteDao.getKey(currentAccountKey, currentReportKey)?.let { copy(remoteKey = it) }

    override suspend fun updateRemoteList(remoteMap: Map<String, FirebaseTransactionModel?>) =
        remoteDao.updateAll(currentAccountKey, currentReportKey, remoteMap)

    private fun String.findTransactionType() =
        TransactionTypes.values().find { it.name == this }

    private fun String?.findCurrency() =
        Currencies.values().find { it.name == this }
}