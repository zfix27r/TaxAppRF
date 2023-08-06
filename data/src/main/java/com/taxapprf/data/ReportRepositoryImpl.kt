package com.taxapprf.data

import com.taxapprf.data.remote.cbrapi.CBRAPI
import com.taxapprf.data.remote.firebase.FirebaseReportDaoImpl
import com.taxapprf.data.remote.firebase.model.FirebaseReportModel
import com.taxapprf.domain.ReportRepository
import com.taxapprf.domain.report.DeleteReportModel
import com.taxapprf.domain.report.ReportAdapterModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val firebaseReportDao: FirebaseReportDaoImpl,
    private val cbrapi: CBRAPI,
) : ReportRepository {
    override fun getReport(accountKey: String): Flow<List<ReportAdapterModel>> = flow {
        emit(
            firebaseReportDao.getReports(accountKey)
                .map { it.toReportAdapterModel() }
        )
    }

    private fun FirebaseReportModel.toReportAdapterModel() =
        ReportAdapterModel(
            name = name ?: "",
            sum = tax ?: ""
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

    override fun deleteReport(deleteReportModel: DeleteReportModel) = flow {
        emit(firebaseReportDao.deleteReport(deleteReportModel))
    }
}