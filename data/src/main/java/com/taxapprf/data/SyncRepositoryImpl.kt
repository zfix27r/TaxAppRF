package com.taxapprf.data

import com.taxapprf.data.local.room.LocalSyncDao
import com.taxapprf.data.remote.firebase.dao.RemoteTransactionDao
import com.taxapprf.data.remote.firebase.dao.RemoteUserDao
import com.taxapprf.data.remote.firebase.entity.FirebaseTransactionEntity
import com.taxapprf.data.sync.SyncAccounts
import com.taxapprf.data.sync.SyncReports
import com.taxapprf.data.sync.SyncTransactions
import com.taxapprf.domain.SyncRepository
import javax.inject.Inject

class SyncRepositoryImpl @Inject constructor(
    private val localSyncDao: LocalSyncDao,

    private val networkManager: NetworkManager,
    private val remoteUserDao: RemoteUserDao,
    private val remoteTransactionDao: RemoteTransactionDao,

    private val syncAccounts: SyncAccounts,
    private val syncReports: SyncReports,
    private val syncTransactions: SyncTransactions,
) : SyncRepository {
    override suspend fun syncAll() {
        networkManager.isConnectionOrThrow()

        syncDeleted()

        remoteUserDao.getUser()?.let { firebaseUser ->
            firebaseUser.email?.let { email ->
                localSyncDao.getUserByEmail(email)?.let { localUserEntity ->
                    syncAccounts.sync(localUserEntity.id).map { getSyncResultAccountModel ->
                        syncReports.sync(getSyncResultAccountModel)
                            .map { getSyncResultReportModel ->
                                syncTransactions.sync(getSyncResultReportModel)
                            }
                    }
                }
            }
        }
    }

    override suspend fun syncDeleted() {
        networkManager.isConnectionOrThrow()

        val updateRemoteMap = mutableMapOf<String, FirebaseTransactionEntity?>()
        var accountKey = ""
        var reportKey = ""

        localSyncDao.getAllDeleted().forEach { localDeletedEntity ->
            if (localDeletedEntity.accountKey != accountKey || localDeletedEntity.reportKey != reportKey) {
                if (updateRemoteMap.isNotEmpty()) {
                    remoteTransactionDao.updateTransactions(accountKey, reportKey, updateRemoteMap)
                    updateRemoteMap.clear()
                }

                accountKey = localDeletedEntity.accountKey
                reportKey = localDeletedEntity.reportKey
            }

            updateRemoteMap[localDeletedEntity.transactionKey] = null
        }

        if (updateRemoteMap.isNotEmpty()) {
            remoteTransactionDao.updateTransactions(accountKey, reportKey, updateRemoteMap)
            localSyncDao.deleteAllDeleted()
        }
    }
}