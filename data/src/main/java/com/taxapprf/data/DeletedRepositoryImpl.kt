package com.taxapprf.data

import com.taxapprf.data.local.room.LocalDeletedDao
import com.taxapprf.data.local.room.entity.LocalDeletedEntity
import com.taxapprf.data.local.room.model.transaction.GetDeletedTransaction
import com.taxapprf.data.remote.firebase.Firebase
import com.taxapprf.data.remote.firebase.dao.RemoteReportDao
import com.taxapprf.data.remote.firebase.dao.RemoteTransactionDao
import com.taxapprf.data.remote.firebase.entity.FirebaseTransactionEntity
import com.taxapprf.domain.DeletedRepository
import javax.inject.Inject

class DeletedRepositoryImpl @Inject constructor(
    private val localDeletedDao: LocalDeletedDao,

    private val networkManager: NetworkManager,
    private val firebase: Firebase,
    private val remoteReportDao: RemoteReportDao,
    private val remoteTransactionDao: RemoteTransactionDao,
) : DeletedRepository {
    override suspend fun deleteReports(reportIds: List<Int>) {
        reportIds.forEach { reportId ->
            val deleteTransactions = localDeletedDao.getGetDeletedTransactionsByReportId(reportId)
            startLocalDeleteTransactions(deleteTransactions)
        }
    }

    override suspend fun deleteTransactions(transactionIds: List<Int>) {
        val deleteTransactions =
            localDeletedDao.getGetDeletedTransactionsByTransactionIds(transactionIds)
        startLocalDeleteTransactions(deleteTransactions)
    }

    private fun startLocalDeleteTransactions(deleteTransactions: List<GetDeletedTransaction>) {
        if (deleteTransactions.isNotEmpty()) {
            localDeletedDao.getGetDeletedReport(deleteTransactions.first().reportId)
                ?.let { getDeletedReport ->
                    var newTax = getDeletedReport.tax
                    var newSize = getDeletedReport.size
                    val localDeletedKeyEntities = mutableListOf<LocalDeletedEntity>()

                    deleteTransactions
                        .forEach { getDeletedTransaction ->
                            getDeletedTransaction.tax?.let { newTax -= it }
                            newSize--

                            getDeletedTransaction.remoteKey?.let {
                                localDeletedKeyEntities.add(
                                    LocalDeletedEntity(
                                        accountKey = getDeletedReport.accountKey,
                                        reportKey = getDeletedReport.reportKey,
                                        transactionKey = getDeletedTransaction.remoteKey,
                                        syncAt = getDeletedTransaction.syncAt
                                    )
                                )
                            }
                        }

                    if (newSize > 1)
                        localDeletedDao
                            .updateLocalReportEntity(getDeletedReport.id, newTax, newSize)
                    else
                        localDeletedDao.deleteLocalReportEntity(getDeletedReport.id)

                    localDeletedDao.saveLocalDeletedEntity(localDeletedKeyEntities)
                    localDeletedDao.deleteLocalTransactionEntities(deleteTransactions.map { it.id })
                }
        }
    }


    private suspend fun remoteReportsDelete() {
        val remoteTransactionMap = mutableMapOf<String, FirebaseTransactionEntity?>()

    }

    private suspend fun deferredRemoteReportsDelete() {

    }

    /*    private suspend fun remoteDeleteTransactions(
            account: LocalAccountEntity,
            report: LocalReportEntity,
            transactions: List<LocalTransactionEntity>
        ) {
            if (firebase.auth.currentUser != null && networkManager.isConnection) {
                remoteReportDao.updateAll(report.remoteKey, report.toMapFirebaseReportModel())

                val remoteTransactionMap = mutableMapOf<String, FirebaseTransactionEntity?>()

                transactions.forEach { transaction ->
                    transaction.remoteKey?.let { remoteTransactionMap[it] = null }
                }

                remoteTransactionDao.updateAll(
                    account.remoteKey,
                    report.remoteKey,
                    remoteTransactionMap
                )
            } else {
                val deletedKeys = mutableListOf<LocalDeletedEntity>()

                transactions.forEach { transaction ->
                    val deletedKey = LocalDeletedEntity(
                        accountKey = account.remoteKey,
                        reportKey = report.remoteKey,
                        transactionKey = transaction.remoteKey,
                        syncAt = transaction.syncAt
                    )
                    deletedKeys.add(deletedKey)
                }

                localDeletedDao.saveDeletedKeys(deletedKeys)
            }
        }*/

    /*    suspend fun deleteReportTransactions(

        ) {
            localDao.getKeysTransactions(deleteReportWithTransactionsModel.reportId)
                .map { transactionKeys ->

                    transactionKeys.transactionKey?.let { transactionKey ->
                        val deletedKey = LocalDeletedKeyEntity(
                            accountKey = transactionKeys.accountKey,
                            reportKey = transactionKeys.reportKey,
                            transactionKey = transactionKey,
                            syncAt = transactionKeys.syncAt
                        )
                        localDeletedKeyDao.save(deletedKey)
                    }

                    localDao.delete(transactionKeys.id)
                }
        }*/
    /*    private suspend fun remoteDeleteTransactions(
            accountKey: String,
            reportKey: String,
            transactionMap: Map<String, FirebaseTransactionEntity?>
        ) {
            if (firebase.auth.currentUser != null && networkManager.isConnection) {
                remoteReportDao.updateAll(report.remoteKey, report.toMapFirebaseReportModel())

                val remoteTransactionMap = mutableMapOf<String, FirebaseTransactionEntity?>()

                transactions.forEach { transaction ->
                    transaction.remoteKey?.let { remoteTransactionMap[it] = null }
                }

                remoteTransactionDao.updateTransactions(
                    account.remoteKey,
                    report.remoteKey,
                    remoteTransactionMap
                )
            } else {

            }
        }

        private fun deferRemoteDeleteTransactions(
            accountKey: String,
            reportKey: String,
            transactionKeyList: List<String>,
            transactionSyncAt: Long
        ) {
            val deletedKeys = transactionKeyList.map { transactionKey ->
                LocalDeletedKeyEntity(
                    accountKey = accountKey,
                    reportKey = reportKey,
                    transactionKey = transactionKey,
                    syncAt = transaction.syncAt
                )
            }

            transactions.forEach { transaction ->
                val deletedKey = LocalDeletedKeyEntity(
                    accountKey = account.remoteKey,
                    reportKey = report.remoteKey,
                    transactionKey = transaction.remoteKey,
                    syncAt = transaction.syncAt
                )
                deletedKeys.add(deletedKey)
            }

            localDao.saveDeletedKeys(deletedKeys)
        }

        private fun LocalReportEntity.toMapFirebaseReportModel() =
            mapOf(
                remoteKey to FirebaseReportEntity(syncAt).apply { key = remoteKey }
            )*/

}