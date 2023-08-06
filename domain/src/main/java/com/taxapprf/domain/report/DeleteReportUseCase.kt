package com.taxapprf.domain.report

import com.taxapprf.domain.ReportRepository
import com.taxapprf.domain.report.DeleteReportModel
import javax.inject.Inject

class DeleteReportUseCase @Inject constructor(
    private val repository: ReportRepository
) {
    fun execute(deleteReportModel: DeleteReportModel) =
        repository.deleteReport(deleteReportModel)
}