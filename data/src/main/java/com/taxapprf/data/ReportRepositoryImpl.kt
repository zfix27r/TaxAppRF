package com.taxapprf.data

import com.taxapprf.data.local.room.LocalDatabase.Companion.DEFAULT_ID
import com.taxapprf.data.local.room.LocalReportDao
import com.taxapprf.data.local.room.entity.LocalReportEntity
import com.taxapprf.data.local.room.entity.LocalReportEntity.Companion.DEFAULT_TAX
import com.taxapprf.domain.ReportRepository
import com.taxapprf.domain.delete.DeleteReportWithTransactionsModel
import com.taxapprf.domain.delete.DeleteTransactionWithReportModel
import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.update.UpdateReportWithTransactionTaxModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepositoryImpl @Inject constructor(
    private val localDao: LocalReportDao,
) : ReportRepository {
    override fun observe(reportId: Int) =
        localDao.observe(reportId).map { it?.toReportModel() }

    override fun observeAll(accountId: Int) =
        localDao.observeAll(accountId).map { reports -> reports.map { it.toReportModel() } }

    override suspend fun updateWithCUDTransaction(
        accountId: Int,
        reportId: Int?,
        transactionId: Int?,
        date: Long,
        transactionTax: Double?
    ): Int {
        val newRemoteKey = date.getYear()

        return reportId?.let {
            localDao.get(reportId)?.let { report ->
                if (report.isRemoveTransaction(newRemoteKey)) {
                    if (report.size.isTransactionLast())
                        report.delete()
                    else
                        report.updateWithDeleteTransaction(transactionTax)

                    addReport(accountId, newRemoteKey)
                } else {
                    if (transactionId.isUpdateTransaction())
                        report.updateWithUpdateTransaction(transactionTax)
                    else
                        report.updateWithAddTransaction()
                }
            }
        } ?: addReport(accountId, newRemoteKey)
    }

    override suspend fun getReport(reportId: Int) =
        localDao.get(reportId)?.toReportModel()

    override suspend fun updateTax(updateReportWithTransactionTaxModel: UpdateReportWithTransactionTaxModel) {
        updateReportWithTransactionTaxModel.reportId?.let { reportId ->
            updateReportWithTransactionTaxModel.tax?.let { tax ->
                localDao.get(reportId)?.let {
                    val newTax = it.tax + tax
                    localDao.updateTax(it.id, newTax)
                }
            }
        }
    }

    override suspend fun deleteReport(
        deleteReportWithTransactionsModel: DeleteReportWithTransactionsModel
    ) =
        localDao.delete(deleteReportWithTransactionsModel.reportId)

    override suspend fun deleteOrUpdateReport(deleteTransactionWithReportModel: DeleteTransactionWithReportModel) {
        with(deleteTransactionWithReportModel) {
            deleteTransactionInReport(reportId, transactionTax)
        }
    }

    override suspend fun saveReport(reportModel: ReportModel) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAll() =
        localDao.deleteAll()

    private fun deleteTransactionInReport(
        reportId: Int?,
        transactionTax: Double? = null,
        newReportKey: String? = null
    ) =
        reportId?.let {
            localDao.get(reportId)?.let { report ->
                if (report.remoteKey != newReportKey) {
                    if (report.size == 1)
                        localDao.delete(reportId)
                    else {
                        val newSize = report.size - 1
                        val newTax = report.tax - (transactionTax ?: DEFAULT_TAX)
                        val updatedReportModel =
                            report.getLocalReportEntity(
                                report.accountId,
                                report.remoteKey,
                                newSize,
                                newTax
                            )
                        localDao.save(updatedReportModel)
                    }
                }
                report
            }
        }

    private fun LocalReportEntity.toReportModel() =
        ReportModel(id, remoteKey, tax, size)


    private fun LocalReportEntity.isRemoveTransaction(newRemoteKey: String) =
        remoteKey != newRemoteKey

    private fun Int.isTransactionLast() =
        this < 2

    private fun LocalReportEntity.delete() =
        localDao.delete(id)

    private fun LocalReportEntity.updateWithDeleteTransaction(transactionTax: Double?) =
        transactionTax?.let {
            updateReport(
                tax = tax - transactionTax,
                size = size - 1
            )
        }

    private fun LocalReportEntity.updateWithUpdateTransaction(transactionTax: Double?) =
        transactionTax?.let {
            updateReport(
                tax = tax - transactionTax,
                size = size
            )
        }

    private fun LocalReportEntity.updateWithAddTransaction() =
        updateReport(
            tax = tax,
            size = size + 1
        )

    private fun Int?.isUpdateTransaction() = this != null

    private fun LocalReportEntity.updateReport(
        tax: Double,
        size: Int
    ) =
        let {
            val updatedLocalReportEntity = LocalReportEntity(
                id = id,
                accountId = accountId,
                tax = tax,
                size = size,
                remoteKey = remoteKey,
                syncAt = syncAt
            )
            localDao.save(updatedLocalReportEntity)
        }.toInt()

    private fun addReport(
        accountId: Int,
        remoteKey: String
    ) =
        localDao.get(accountId, remoteKey)
            ?.updateWithAddTransaction()
            ?: run {
                val newLocalReportEntity = LocalReportEntity(
                    accountId = accountId,
                    tax = DEFAULT_TAX,
                    size = 1,
                    remoteKey = remoteKey,
                    syncAt = getEpochTime()
                )
                localDao.save(newLocalReportEntity)
            }.toInt()

    private fun LocalReportEntity?.getLocalReportEntity(
        accountId: Int,
        remoteKey: String,
        size: Int? = null,
        tax: Double? = null,
    ): LocalReportEntity {
        return LocalReportEntity(
            id = this?.id ?: DEFAULT_ID,
            accountId = accountId,
            tax = tax ?: this?.tax ?: DEFAULT_TAX,
            size = size ?: LocalReportEntity.DEFAULT_SIZE,
            remoteKey = remoteKey,
            syncAt = this?.syncAt ?: getEpochTime()
        )
    }
}