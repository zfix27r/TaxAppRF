package com.taxapprf.data

import com.taxapprf.data.local.excel.ExcelDaoImpl
import com.taxapprf.data.local.excel.ExcelParcel
import com.taxapprf.data.remote.firebase.FirebaseReportDaoImpl
import com.taxapprf.domain.ReportRepository
import com.taxapprf.domain.report.DeleteReportModel
import com.taxapprf.domain.report.GetReportsModel
import com.taxapprf.domain.report.SaveExcelToFirebaseModel
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

    override fun saveReportsFromExcel(saveReportsFromExcelModel: SaveExcelToFirebaseModel) = flow {
        ExcelParcel(saveReportsFromExcelModel.filePath)
            .parse()
/*            .map { transaction ->
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
}