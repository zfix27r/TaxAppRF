package com.taxapprf.data.sync

import com.taxapprf.data.calculateTax
import com.taxapprf.data.local.room.LocalDatabase.Companion.DEFAULT_ID
import com.taxapprf.data.local.room.LocalSyncDao
import com.taxapprf.data.local.room.entity.LocalTransactionEntity
import com.taxapprf.data.local.room.model.sync.GetSyncResultReportModel
import com.taxapprf.data.local.room.model.sync.GetSyncTransactionModel
import com.taxapprf.data.remote.firebase.dao.RemoteReportDao
import com.taxapprf.data.remote.firebase.dao.RemoteTransactionDao
import com.taxapprf.data.remote.firebase.entity.FirebaseTransactionEntity
import com.taxapprf.domain.CurrencyRepository
import com.taxapprf.domain.currency.Currencies
import com.taxapprf.domain.transactions.TransactionTypes
import kotlinx.coroutines.runBlocking

class SyncTransactions(
    private val localSyncDao: LocalSyncDao,
    private val remoteReportDao: RemoteReportDao,
    private val remoteTransactionDao: RemoteTransactionDao,
    private val currencyRepository: CurrencyRepository,
) : SyncManager<GetSyncTransactionModel, LocalTransactionEntity, FirebaseTransactionEntity>() {
    private var currentReportId: Int = 0

    private var currentAccountKey: String = ""
    private var currentReportKey: String = ""

    suspend fun sync(getSyncResultReportModel: GetSyncResultReportModel) {
        currentReportId = getSyncResultReportModel.reportId
        currentAccountKey = getSyncResultReportModel.accountKey
        currentReportKey = getSyncResultReportModel.reportKey

        startSync()
        deleteEmptyReport()
    }

    override fun GetSyncTransactionModel.toRemote(): FirebaseTransactionEntity {
        val firebaseTransactionModel =
            FirebaseTransactionEntity(
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

    override fun FirebaseTransactionEntity.toLocalOut(localIn: GetSyncTransactionModel?): LocalTransactionEntity? {
        val transactionKey = key ?: return null
        val date = date ?: return null
        val transactionTypeOrdinal = type?.findTransactionType()?.ordinal ?: return null
        val currencyOrdinal = currency?.findCurrency()?.ordinal ?: return null
        val sum = sum ?: return null
        val syncAt = syncAt ?: 0

        val tax =
            runBlocking {
                currencyRepository.getCurrencyRate(currencyOrdinal, date)?.let { rate ->
                    calculateTax(sum, rate, transactionTypeOrdinal)
                }
            }

        return LocalTransactionEntity(
            id = localIn?.transactionId ?: DEFAULT_ID,
            reportId = currentReportId,
            typeOrdinal = transactionTypeOrdinal,
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
        localSyncDao.getTransactions(currentReportId)

    override fun deleteLocalOutList(locals: List<LocalTransactionEntity>) =
        localSyncDao.deleteTransactionsWithUpdateReport(currentReportId, locals)

    override fun saveLocalOutList(locals: List<LocalTransactionEntity>) =
        localSyncDao.saveTransactionsWithUpdateReport(currentReportId, locals)

    override suspend fun getRemoteList() =
        remoteTransactionDao.getAll(currentAccountKey, currentReportKey)

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
            isSync = true,
            syncAt = syncAt
        )

    override suspend fun GetSyncTransactionModel.updateRemoteKey() =
        remoteTransactionDao.getKey(currentAccountKey, currentReportKey)
            ?.let { copy(remoteKey = it) }

    override suspend fun updateRemoteList(remoteMap: Map<String, FirebaseTransactionEntity?>) =
        remoteTransactionDao.updateTransactions(currentAccountKey, currentReportKey, remoteMap)

    private suspend fun deleteEmptyReport() {
        localSyncDao.getReport(currentReportId)?.let { localReportEntity ->
            if (localReportEntity.size == 0) {
                val updateRemoteMap = mapOf(localReportEntity.remoteKey to null)
                remoteReportDao.updateAll(currentAccountKey, updateRemoteMap)
                localSyncDao.deleteReport(localReportEntity)
            }
        }
    }

    private fun String.findTransactionType() =
        TransactionTypes.values().find { it.name == this }

    private fun String?.findCurrency() =
        Currencies.values().find { it.name == this }
}