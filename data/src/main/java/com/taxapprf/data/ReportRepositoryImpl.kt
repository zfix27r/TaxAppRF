package com.taxapprf.data

import com.taxapprf.data.local.room.LocalReportDao
import com.taxapprf.data.local.room.entity.LocalReportEntity
import com.taxapprf.data.local.room.entity.LocalReportEntity.Companion.DEFAULT_TAX
import com.taxapprf.data.sync.DEFAULT_SYNC_AT
import com.taxapprf.domain.ReportRepository
import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.tax.UpdateTaxModel
import com.taxapprf.domain.transaction.DeleteTransactionModel
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

    override suspend fun delete(deleteTransactionModel: DeleteTransactionModel) =
        with(deleteTransactionModel) {
            deleteTransactionInReport(reportId, transactionTax)
        }

    override suspend fun deleteAll(accountId: Int) = 1
    //localDao.delete(accountId)

    override suspend fun getReportId(
        accountId: Int,
        reportId: Int?,
        date: Long,
        transactionTax: Double?
    ): Int {
        val newReportKey = date.getYear()
        var newReportId = reportId ?: 0

        deleteTransactionInReport(reportId, transactionTax, newReportKey)

        localDao.get(accountId, newReportKey)?.let { report ->
            newReportId = report.id

            val newSize = report.size + 1
            val updatedReportModel = report.getLocalReportEntity(accountId, newReportKey, newSize)
            localDao.save(updatedReportModel)
        } ?: run {
            val updatedReportModel = null.getLocalReportEntity(accountId, newReportKey)
            newReportId = localDao.save(updatedReportModel).toInt()
        }

        return newReportId
    }

    override suspend fun updateTax(updateTaxModel: UpdateTaxModel) {
        updateTaxModel.reportId?.let { reportId ->
            updateTaxModel.tax?.let { tax ->
                localDao.get(reportId)?.let {
                    val newTax = it.tax + tax - (updateTaxModel.oldTax ?: 0.0)
                    localDao.updateTax(it.id, newTax)
                }
            }
        }
    }

    private fun deleteTransactionInReport(
        reportId: Int?,
        transactionTax: Double? = null,
        newReportKey: String? = null
    ) {
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
            }
        }
    }

    private fun LocalReportEntity.toReportModel() =
        ReportModel(id, remoteKey, tax, size)

    private fun LocalReportEntity?.getLocalReportEntity(
        accountId: Int,
        remoteKey: String,
        size: Int? = null,
        tax: Double? = null,
    ): LocalReportEntity {
        return LocalReportEntity(
            id = this?.id ?: LocalReportEntity.DEFAULT_ID,
            accountId = accountId,
            tax = tax ?: this?.tax ?: DEFAULT_TAX,
            size = size ?: LocalReportEntity.DEFAULT_SIZE,
            remoteKey = remoteKey,
            syncAt = this?.syncAt ?: DEFAULT_SYNC_AT
        )
    }
}