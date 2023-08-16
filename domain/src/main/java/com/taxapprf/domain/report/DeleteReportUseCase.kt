package com.taxapprf.domain.report

import com.taxapprf.domain.ReportRepository
import javax.inject.Inject

class DeleteReportUseCase @Inject constructor(
    private val repository: ReportRepository
) {
    fun execute(deleteReportModel: DeleteReportModel) =
        repository.deleteReport(deleteReportModel)
}