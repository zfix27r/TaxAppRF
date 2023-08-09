package com.taxapprf.data.remote.firebase.dao

import com.taxapprf.data.remote.firebase.model.FirebaseReportModel
import com.taxapprf.domain.FirebasePathModel
import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.report.SaveReportModel
import kotlinx.coroutines.flow.Flow

interface FirebaseReportDao {
    fun getReports(accountKey: String): Flow<List<ReportModel>>
    suspend fun addReport(saveReportModel: SaveReportModel)
    suspend fun updateReport(saveReportModel: SaveReportModel)
    suspend fun deleteReport(firebasePathModel: FirebasePathModel)

    suspend fun getReportTax(firebasePathModel: FirebasePathModel): FirebaseReportModel?
    suspend fun saveReportTax(
        firebasePathModel: FirebasePathModel,
        firebaseReportModel: FirebaseReportModel
    )
}