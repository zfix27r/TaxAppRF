package com.taxapprf.data

import com.taxapprf.data.local.excel.ExcelDaoImpl
import com.taxapprf.data.remote.cbrapi.CBRAPI
import com.taxapprf.data.remote.firebase.FirebaseReportDaoImpl
import com.taxapprf.domain.FirebasePathModel
import com.taxapprf.domain.ReportRepository
import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.transaction.TransactionModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val firebaseReportDao: FirebaseReportDaoImpl,
    private val excelDao: ExcelDaoImpl,
    private val cbrapi: CBRAPI,
) : ReportRepository {
    override fun getReports(accountName: String) = firebaseReportDao.getReports(accountName)

    override fun saveReportFromExcel(storagePath: String) = flow<Unit> {
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
    }

    override fun getExcelReport(
        report: ReportModel,
        transactions: List<TransactionModel>
    ) = excelDao.sendReport(report, transactions)

    override fun deleteReport(firebasePathModel: FirebasePathModel) = flow {

        emit(firebaseReportDao.deleteReport(firebasePathModel))
    }
}