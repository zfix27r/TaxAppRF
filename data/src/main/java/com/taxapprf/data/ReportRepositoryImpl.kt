package com.taxapprf.data

import android.net.Uri
import com.taxapprf.data.local.excel.ExcelDaoImpl
import com.taxapprf.data.remote.firebase.FirebaseReportDaoImpl
import com.taxapprf.domain.ReportRepository
import com.taxapprf.domain.report.DeleteReportModel
import com.taxapprf.domain.report.GetReportsModel
import com.taxapprf.domain.report.GetReportsUriModel
import com.taxapprf.domain.report.SaveReportsFromUriModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val firebaseReportDao: FirebaseReportDaoImpl,
    private val excelDao: ExcelDaoImpl,
) : ReportRepository {
    override fun getReports(getReportsModel: GetReportsModel) =
        firebaseReportDao.getReports(getReportsModel)

    override fun deleteReport(deleteReportModel: DeleteReportModel) = flow {
        emit(firebaseReportDao.deleteReport(deleteReportModel))
    }

    override fun saveReportsFromUri(saveReportsFromUriModel: SaveReportsFromUriModel) = flow {
        /*        ExcelParcel(storagePath)
                    .parse()
                    .map { transaction ->
                        getCBRRate(transaction.date, transaction.currency).collectLatest {
                            transaction.rateCentralBank = it
                            transaction.calculateSumRub()
                            firebaseAPI.saveTransaction(transaction)
                            firebaseAPI.sumTaxes()
                            emit(Unit)
                        }
                    }*/

        emit(Unit)
    }

    override fun getReportsUri(getReportsUriModel: GetReportsUriModel) = flow<Uri> {
        with(getReportsUriModel) {
//            excelDao.sendReport(report, transactions)
        }
    }
}