package com.taxapprf.domain

import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.transaction.DeleteTransactionModel
import com.taxapprf.domain.transaction.SaveTransactionModel
import kotlinx.coroutines.flow.Flow

interface ReportRepository {
    fun observe(accountKey: String, reportKey: String): Flow<ReportModel?>
    fun observeAll(accountKey: String): Flow<List<ReportModel>>
    suspend fun delete(id: Int)
    suspend fun deleteAll(ids: List<Int>)
    suspend fun updateAfterUpdateTransaction(saveTransactionModel: SaveTransactionModel)
    suspend fun updateAfterUpdateTransaction(deleteTransactionModel: DeleteTransactionModel)
}