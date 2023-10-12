package com.taxapprf.data

import com.taxapprf.data.error.DataErrorConnection
import com.taxapprf.data.local.room.LocalMainDao
import com.taxapprf.data.remote.firebase.dao.RemoteUserDao
import com.taxapprf.data.sync.SyncAccounts
import com.taxapprf.data.sync.SyncReports
import com.taxapprf.data.sync.SyncTransactions
import com.taxapprf.domain.SyncRepository
import javax.inject.Inject

class SyncRepositoryImpl @Inject constructor(
    private val networkManager: NetworkManager,
    private val syncAccounts: SyncAccounts,
    private val syncReports: SyncReports,
    private val syncTransactions: SyncTransactions,
    private val localMainDao: LocalMainDao,
    private val remoteUserDao: RemoteUserDao,
) : SyncRepository {
    override suspend fun syncAll() {
        if (networkManager.isConnection) {
            remoteUserDao.getUser()?.let { firebaseUser ->
                firebaseUser.email?.let { email ->
                    localMainDao.getUserByEmail(email)?.let { localUserEntity ->
                        syncAccounts.sync(localUserEntity.id).map { getSyncResultAccountModel ->
                            println(getSyncResultAccountModel)
                            syncReports.sync(getSyncResultAccountModel).map { getSyncResultReportModel ->
                                println(getSyncResultReportModel)
                                syncTransactions.sync(getSyncResultReportModel)
                                println("TRANSACTION_SYNC")
                            }
                        }
                    }
                }
            }
        } else throw DataErrorConnection()
    }
}