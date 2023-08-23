package com.taxapprf.data

import com.taxapprf.data.remote.firebase.FirebaseReportDaoImpl
import com.taxapprf.domain.ReportRepository
import com.taxapprf.domain.report.DeleteReportModel
import com.taxapprf.domain.report.GetReportsModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val firebaseReportDao: FirebaseReportDaoImpl,
) : ReportRepository {
    override fun getReports(getReportsModel: GetReportsModel) =
        firebaseReportDao.getReports(getReportsModel)

    override fun deleteReport(deleteReportModel: DeleteReportModel) = flow {
        emit(firebaseReportDao.deleteReport(deleteReportModel))
    }
}