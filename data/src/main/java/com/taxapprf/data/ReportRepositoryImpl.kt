package com.taxapprf.data

import com.taxapprf.data.error.DataErrorResponseEmpty
import com.taxapprf.data.remote.cbrapi.CBRAPI
import com.taxapprf.data.remote.firebase.FirebaseReportDaoImpl
import com.taxapprf.data.remote.firebase.model.FirebaseReportModel
import com.taxapprf.domain.FirebasePathModel
import com.taxapprf.domain.ReportRepository
import com.taxapprf.domain.report.ReportModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val firebaseReportDao: FirebaseReportDaoImpl,
    private val cbrapi: CBRAPI,
) : ReportRepository {
    override fun getReports(accountKey: String) = flow {
        emit(
            firebaseReportDao.getReports(accountKey)
                .map { it.toReportModel() }
        )
    }

    private fun FirebaseReportModel.toReportModel() =
        ReportModel(
            year = year ?: throw DataErrorResponseEmpty(),
            tax = tax ?: throw DataErrorResponseEmpty()
        )

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

    override fun deleteReport(firebasePathModel: FirebasePathModel) = flow {
        emit(firebaseReportDao.deleteReport(firebasePathModel))
    }
}