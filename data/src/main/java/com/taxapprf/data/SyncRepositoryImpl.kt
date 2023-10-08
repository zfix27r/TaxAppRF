package com.taxapprf.data

import com.taxapprf.data.error.DataErrorConnection
import com.taxapprf.data.sync.SyncAccounts
import com.taxapprf.data.sync.SyncReports
import com.taxapprf.data.sync.SyncTransactions
import com.taxapprf.domain.SyncRepository
import com.taxapprf.domain.delete.DeleteTransactionWithReportModel
import javax.inject.Inject

class SyncRepositoryImpl @Inject constructor(
    private val networkManager: NetworkManager,
    private val syncAccounts: SyncAccounts,
    private val syncReports: SyncReports,
    private val syncTransactions: SyncTransactions,
) : SyncRepository {
    override suspend fun syncAll(userId: Int) {
        if (networkManager.available) {
            syncAccounts.sync(userId).map { getSyncResultAccountModel ->
                println(getSyncResultAccountModel)
                syncReports.sync(getSyncResultAccountModel).map { getSyncResultReportModel ->
                    println(getSyncResultReportModel)
                    syncTransactions.sync(getSyncResultReportModel)
                    println("TRANSACTION_SYNC")
                }
            }

        } else throw DataErrorConnection()
    }

    override suspend fun syncDeleteTransaction(deleteTransactionWithReportModel: DeleteTransactionWithReportModel) {
        TODO("Not yet implemented")
    }
}