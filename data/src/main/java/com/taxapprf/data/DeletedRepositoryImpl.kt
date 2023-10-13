package com.taxapprf.data

import com.taxapprf.data.local.room.LocalDeletedDao
import com.taxapprf.data.local.room.entity.LocalDeletedEntity
import com.taxapprf.data.local.room.model.transaction.GetDeletedTransaction
import com.taxapprf.domain.DeletedRepository
import javax.inject.Inject

class DeletedRepositoryImpl @Inject constructor(
    private val localDeletedDao: LocalDeletedDao,
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

                    if (newSize > 0)
                        localDeletedDao
                            .updateLocalReportEntity(getDeletedReport.id, newTax, newSize)
                    else
                        localDeletedDao.deleteLocalReportEntity(getDeletedReport.id)

                    localDeletedDao.saveLocalDeletedEntity(localDeletedKeyEntities)
                    localDeletedDao.deleteLocalTransactionEntities(deleteTransactions.map { it.id })
                }
        }
    }
}