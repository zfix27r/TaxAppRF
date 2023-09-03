package com.taxapprf.data

import com.taxapprf.data.error.external.DataErrorExternalGetReport
import com.taxapprf.data.local.room.dao.LocalReportDao
import com.taxapprf.data.local.room.entity.LocalReportEntity
import com.taxapprf.data.remote.firebase.FirebaseReportDaoImpl
import com.taxapprf.domain.ReportRepository
import com.taxapprf.domain.report.DeleteReportModel
import com.taxapprf.domain.report.ObserveReportModel
import com.taxapprf.domain.report.ObserveReportsModel
import com.taxapprf.domain.report.ReportModel
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val reportDao: LocalReportDao,
    private val firebaseReportDao: FirebaseReportDaoImpl,
) : ReportRepository {
    override fun observeReport(observeReportModel: ObserveReportModel) =
        firebaseReportDao.observeReport(observeReportModel).map {
            it.getOrElse { throw DataErrorExternalGetReport() }
        }

    override fun observeReports(observeReportsModel: ObserveReportsModel) = channelFlow {
        observeLocalReports(observeReportsModel.accountKey).collectLatest {
            send(it)
        }

        observeRemoteReports(observeReportsModel).collect()
    }

    private fun observeLocalReports(accountKey: String) =
        reportDao.observeAll(accountKey).map { it.toListReportModel() }

    private fun observeRemoteReports(observeReportsModel: ObserveReportsModel) =
        firebaseReportDao.observeReports(observeReportsModel).map { result ->
            println(result)
            result.getOrNull()?.let {
                //reportDao.delete(it.toListReportEntity(observeReportsModel.accountKey))
                val res =
                    reportDao.save(it.toListReportEntity(observeReportsModel.accountKey))
                println("@@@@@@@@@@@@ $res")
            }
        }


    override fun deleteReport(deleteReportModel: DeleteReportModel) = flow {
        emit(firebaseReportDao.deleteReport(deleteReportModel))
    }

    private fun List<LocalReportEntity>.toListReportModel() =
        map { ReportModel(it.yearKey, it.tax, it.size) }

    private fun List<ReportModel>.toListReportEntity(accountKey: String) =
        map { LocalReportEntity(accountKey, it.year, it.tax, it.size) }
}