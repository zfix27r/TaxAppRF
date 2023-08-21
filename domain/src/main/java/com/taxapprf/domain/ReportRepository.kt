package com.taxapprf.domain

import android.net.Uri
import com.taxapprf.domain.report.DeleteReportModel
import com.taxapprf.domain.report.GetReportsModel
import com.taxapprf.domain.report.GetReportsUriModel
import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.report.SaveReportsFromUriModel
import kotlinx.coroutines.flow.Flow

interface ReportRepository {
    fun getReports(getReportsModel: GetReportsModel): Flow<Result<List<ReportModel>>>
    fun deleteReport(deleteReportModel: DeleteReportModel): Flow<Unit>
    fun saveReportsFromUri(saveReportsFromUriModel: SaveReportsFromUriModel): Flow<Unit>
    fun getReportsUri(getReportsUriModel: GetReportsUriModel): Flow<Uri>
}