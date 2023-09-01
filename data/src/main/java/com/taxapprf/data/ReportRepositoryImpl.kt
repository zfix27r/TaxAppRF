package com.taxapprf.data

import com.taxapprf.data.error.external.DataErrorExternalGetReport
import com.taxapprf.data.remote.firebase.FirebaseReportDaoImpl
import com.taxapprf.domain.ReportRepository
import com.taxapprf.domain.report.DeleteReportModel
import com.taxapprf.domain.report.ObserveReportModel
import com.taxapprf.domain.report.ObserveReportsModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val firebaseReportDao: FirebaseReportDaoImpl,
) : ReportRepository {
    override fun observeReport(observeReportModel: ObserveReportModel) =
        firebaseReportDao.observeReport(observeReportModel).map {
            it.getOrElse { throw DataErrorExternalGetReport() }
        }

    override fun observeReports(observeReportsModel: ObserveReportsModel) =
        firebaseReportDao.observeReports(observeReportsModel).map {
            it.getOrElse { throw DataErrorExternalGetReport() }
        }

    override fun deleteReport(deleteReportModel: DeleteReportModel) = flow {
        emit(firebaseReportDao.deleteReport(deleteReportModel))
    }
}