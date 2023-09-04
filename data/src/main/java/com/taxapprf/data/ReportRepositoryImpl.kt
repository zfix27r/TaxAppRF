package com.taxapprf.data

import com.taxapprf.data.local.room.dao.LocalReportDao
import com.taxapprf.data.local.room.entity.LocalReportEntity
import com.taxapprf.data.remote.firebase.FirebaseReportDaoImpl
import com.taxapprf.data.remote.firebase.model.FirebaseReportModel
import com.taxapprf.domain.ReportRepository
import com.taxapprf.domain.report.DeleteReportModel
import com.taxapprf.domain.report.ObserveReportModel
import com.taxapprf.domain.report.ObserveReportsModel
import com.taxapprf.domain.report.ReportModel
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val localReportDao: LocalReportDao,
    private val firebaseReportDao: FirebaseReportDaoImpl,
) : ReportRepository {
    override fun observeReport(observeReportModel: ObserveReportModel) = channelFlow {
        val local = mutableMapOf<String, ReportModel>()

        launch {
            localReportDao.observe(observeReportModel.accountKey, observeReportModel.yearKey)
                .collectLatest { report ->
                    report.toReportModel().let {
                        local.clear()
                        local[report.key] = it
                        send(it)
                    }
                }
        }

        launch {
            firebaseReportDao.observeReport(observeReportModel).collectLatest { result ->
                result.getOrNull()?.let { report ->
                    local.sync(
                        listOf(report),
                        saveLocal = {
                            localReportDao.save(it.toListLocalReportEntity(observeReportModel.accountKey))
                        },
                        deleteLocal = {
                            localReportDao.delete(it.toListLocalReportEntity(observeReportModel.accountKey))
                        },
                        saveRemote = {
                            launch {
                                firebaseReportDao.saveReports(it.toMapFirebaseReportModel())
                            }
                        }
                    )
                }
            }
        }
    }

    override fun observeReports(observeReportsModel: ObserveReportsModel) = channelFlow {
        val local = mutableMapOf<String, ReportModel>()

        launch {
            localReportDao.observeAll(observeReportsModel.accountKey).collectLatest { reports ->
                local.clear()
                send(
                    reports.map {
                        val report = it.toReportModel()
                        local[report.key] = report
                        report
                    }
                )
            }
        }

        launch {
            firebaseReportDao.observeReports(observeReportsModel).collectLatest { result ->
                result.getOrNull()?.let { reports ->
                    local.sync(
                        reports,
                        saveLocal = {
                            localReportDao.save(it.toListLocalReportEntity(observeReportsModel.accountKey))
                        },
                        deleteLocal = {
                            localReportDao.delete(it.toListLocalReportEntity(observeReportsModel.accountKey))
                        },
                        saveRemote = {
                            launch {
                                firebaseReportDao.saveReports(it.toMapFirebaseReportModel())
                            }
                        }
                    )
                }
            }
        }
    }

    override fun deleteReport(deleteReportModel: DeleteReportModel) = flow {
        emit(firebaseReportDao.deleteReport(deleteReportModel))
    }

    private fun LocalReportEntity.toReportModel() =
        ReportModel(key, tax, size, isSync, syncAt)

    private fun List<ReportModel>.toListLocalReportEntity(accountKey: String) =
        map {
            LocalReportEntity(
                key = it.key,
                accountKey = accountKey,
                tax = it.tax,
                size = it.size,
                isSync = it.isSync,
                syncAt = it.syncAt
            )
        }

    private fun List<ReportModel>.toMapFirebaseReportModel(): MutableMap<String, FirebaseReportModel> {
        val accounts = mutableMapOf<String, FirebaseReportModel>()
        map {
            accounts.put(
                it.key, FirebaseReportModel(
                    key = it.key,
                    tax = it.tax,
                    size = it.size,
                    syncAt = it.syncAt
                )
            )
        }
        return accounts
    }
}