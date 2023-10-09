package com.taxapprf.domain

import com.taxapprf.domain.delete.DeleteTransactionWithReportModel

interface SyncRepository {
    suspend fun syncAll()
    suspend fun syncDeleteTransaction(deleteTransactionWithReportModel: DeleteTransactionWithReportModel)
}