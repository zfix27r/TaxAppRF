package com.taxapprf.data.remote.firebase.dao

import com.taxapprf.data.remote.firebase.model.FirebaseReportModel
import com.taxapprf.data.remote.firebase.model.GetReportModel
import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.report.SaveReportModel
import kotlinx.coroutines.flow.Flow

interface RemoteReportDao {
    fun observe(accountKey: String, reportKey: String): Flow<Result<List<ReportModel>>>
    fun observeAll(accountKey: String): Flow<Result<List<ReportModel>>>
    suspend fun get(getReportModel: GetReportModel): ReportModel
    suspend fun save(saveReportModel: SaveReportModel)
    suspend fun saveAll(
        accountKey: String,
        mapFirebaseReportModels: Map<String, FirebaseReportModel>
    )

    suspend fun delete(accountKey: String, reportKey: String)
    suspend fun deleteAll(
        accountKey: String,
        mapFirebaseReportModels: Map<String, FirebaseReportModel?>
    )
}