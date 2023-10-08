package com.taxapprf.data

import com.taxapprf.data.local.room.LocalReportDao
import com.taxapprf.data.local.room.entity.LocalReportEntity
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
        localDao.observeReport(reportId).map { it?.toReportModel() }

    override fun observeAll(accountId: Int) =
        localDao.observeAccountReports(accountId)
            .map { reports -> reports.map { it.toReportModel() } }

    override suspend fun updateWithCUDTransaction(
        accountId: Int,
        reportId: Int?,
        transactionId: Int?,
        date: Long,
        transactionTax: Double?
    ): Int {
        val newRemoteKey = date.getYear()

        return reportId?.let {
            localDao.getReport(reportId)?.let { report ->
                if (report.isRemoveTransaction(newRemoteKey)) {
                    if (report.size.isTransactionLast())
                        report.delete()
                    else
                        report.updateWithDeleteTransaction(transactionTax)

                    addReport(accountId, newRemoteKey)
                } else {
                    if (transactionId.isUpdateTransaction())
                        report.updateWithStartUpdateTransaction(transactionTax)
                    else
                        report.updateWithAddTransaction()
                }
            }
        } ?: addReport(accountId, newRemoteKey)
    }

    override suspend fun getReport(reportId: Int) =
        localDao.getReport(reportId)?.toReportModel()

    override suspend fun updateTax(updateReportWithTransactionTaxModel: UpdateReportWithTransactionTaxModel) {
        updateReportWithTransactionTaxModel.reportId?.let { reportId ->
            updateReportWithTransactionTaxModel.tax?.let { transactionTax ->
                localDao.getReport(reportId)?.updateWithEndUpdateTransaction(transactionTax)
            }
        }
    }

    override suspend fun deleteReport(
        deleteReportWithTransactionsModel: DeleteReportWithTransactionsModel
    ) =
        localDao.delete(deleteReportWithTransactionsModel.reportId)

    override suspend fun deleteOrUpdateReport(deleteTransactionWithReportModel: DeleteTransactionWithReportModel) {
        deleteTransactionWithReportModel.reportId?.let { reportId ->
            deleteTransactionInReport(reportId, deleteTransactionWithReportModel.transactionTax)
        }
    }

    override suspend fun saveReport(reportModel: ReportModel) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAll() =
        localDao.deleteAll()

    private fun deleteTransactionInReport(
        reportId: Int,
        transactionTax: Double? = null,
        newReportName: String? = null
    ) =
        localDao.getReport(reportId)?.let { report ->
            if (report.remoteKey != newReportName) {
                if (report.size == 1)
                    localDao.delete(reportId)
                else {
                    val newTax = report.tax - (transactionTax ?: DEFAULT_TAX)
                    val newSize = report.size - 1
                    localDao.updateReport(reportId, newTax, newSize)
                }
            }
            report
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
            val newTax = tax - transactionTax
            val newSize = size - 1
            localDao.updateReport(id, newTax, newSize)
        }

    private fun LocalReportEntity.updateWithStartUpdateTransaction(transactionTax: Double?) =
        transactionTax?.let {
            val newTax = tax - transactionTax
            localDao.updateReport(id, newTax, size)
        }

    private fun LocalReportEntity.updateWithEndUpdateTransaction(transactionTax: Double?) =
        transactionTax?.let {
            val newTax = tax + transactionTax
            localDao.updateReport(id, newTax, size)
        }

    private fun LocalReportEntity.updateWithAddTransaction() =
        let {
            val newSize = size + 1
            localDao.updateReport(id, tax, newSize)
        }

    private fun Int?.isUpdateTransaction() = this != null

    private fun addReport(
        accountId: Int,
        name: String
    ) =
        localDao.getReport(accountId, name)
            ?.updateWithAddTransaction()
            ?: run {
                val newLocalReportEntity = LocalReportEntity(
                    accountId = accountId,
                    remoteKey = name,
                    tax = DEFAULT_TAX,
                    size = 1,
                )
                localDao.save(newLocalReportEntity)
            }.toInt()

    companion object {
        const val DEFAULT_TAX = 0.0
    }
}