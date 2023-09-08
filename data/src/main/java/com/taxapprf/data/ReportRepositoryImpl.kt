package com.taxapprf.data

import com.taxapprf.data.local.room.dao.LocalReportDao
import com.taxapprf.data.remote.firebase.FirebaseReportDaoImpl
import com.taxapprf.data.sync.SyncReport
import com.taxapprf.data.sync.SyncReports
import com.taxapprf.domain.ReportRepository
import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.transaction.SaveTransactionModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepositoryImpl @Inject constructor(
    private val localDao: LocalReportDao,
    private val remoteDao: FirebaseReportDaoImpl,
) : ReportRepository {
    override fun observe(accountKey: String, reportKey: String): Flow<ReportModel?> =
        SyncReport(localDao, remoteDao, accountKey, reportKey).observe()

    override fun observeAll(accountKey: String): Flow<List<ReportModel>> =
        SyncReports(localDao, remoteDao, accountKey).observeAll()

    override suspend fun delete(id: Int) {
        localDao.deleteDeferred(id)
    }

    override suspend fun deleteAll(ids: List<Int>) {
        localDao.deleteAllDeferred(ids)
    }

    override suspend fun updateAfterSaveTransaction(
        saveTransactionModel: SaveTransactionModel
    ) {
        with(saveTransactionModel) {
            deleteOrUpdateOldReport()
            updateNewReport()
        }
    }

    private fun SaveTransactionModel.deleteOrUpdateOldReport() {
        transactionKey?.let {
            reportKey?.let { reportKey ->
                localDao.get(accountKey, reportKey)?.let { oldReport ->
                    val newSize = oldReport.size - 1

                    if (newSize == 0) {
                        localDao.deleteDeferred(accountKey, reportKey)
                    } else {
                        val newTax = oldReport.tax - (tax ?: 0.0)
                        localDao.save(
                            oldReport.copy(
                                size = newSize,
                                tax = newTax,
                                isSync = false,
                                syncAt = getTime()
                            )
                        )
                    }
                }
            }
        }
    }

    private fun SaveTransactionModel.updateNewReport() {
        localDao.get(accountKey, newReportKey)?.let { newReport ->
            localDao.save(
                newReport.copy(
                    size = newReport.size + 1,
                    tax = newReport.tax + (tax ?: 0.0),
                    isSync = false,
                    syncAt = getTime()
                )
            )
        }
    }
}