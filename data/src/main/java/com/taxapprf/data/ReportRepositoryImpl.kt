package com.taxapprf.data

import com.taxapprf.data.remote.firebase.FirebaseReportDaoImpl
import com.taxapprf.domain.ReportRepository
import com.taxapprf.domain.report.DeleteReportModel
import com.taxapprf.domain.report.ObserveReportsModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val firebaseReportDao: FirebaseReportDaoImpl,
) : ReportRepository {
    override fun observeReports(getReportsModel: ObserveReportsModel) =
        firebaseReportDao.getReports(getReportsModel)

    override fun deleteReport(deleteReportModel: DeleteReportModel) = flow {
        emit(firebaseReportDao.deleteReport(deleteReportModel))
    }
}