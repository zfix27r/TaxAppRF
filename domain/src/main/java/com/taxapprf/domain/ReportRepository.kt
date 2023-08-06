package com.taxapprf.domain

import com.taxapprf.domain.taxes.ReportAdapterModel
import kotlinx.coroutines.flow.Flow

interface ReportRepository {
    fun getReport(accountKey: String): Flow<List<ReportAdapterModel>>
    fun deleteReport(request: FirebaseRequestModel): Flow<Unit>
    fun saveReportFromExcel(storagePath: String): Flow<Unit>
}