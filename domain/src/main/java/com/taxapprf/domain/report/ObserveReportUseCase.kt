package com.taxapprf.domain.report

import com.taxapprf.domain.ReportRepository
import javax.inject.Inject

class ObserveReportUseCase @Inject constructor(
    private val repository: ReportRepository
) {
    fun execute(reportId: Int) =
        repository.observe(reportId)
}