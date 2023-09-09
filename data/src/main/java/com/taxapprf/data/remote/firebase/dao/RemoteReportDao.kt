package com.taxapprf.data.remote.firebase.dao

import com.taxapprf.data.remote.firebase.model.FirebaseReportModel
import com.taxapprf.data.remote.firebase.model.GetReportModel
import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.report.SaveReportModel
import kotlinx.coroutines.flow.Flow

interface RemoteReportDao {
    fun observe(accountKey: String, reportKey: String): Flow<Result<FirebaseReportModel?>>
    fun observeAll(accountKey: String): Flow<Result<List<FirebaseReportModel>>>
    suspend fun saveAll(accountKey: String, reportModels: List<FirebaseReportModel>)
    suspend fun delete(accountKey: String, reportKey: String)
    suspend fun deleteAll(accountKey: String, reportModels: List<FirebaseReportModel>)
}