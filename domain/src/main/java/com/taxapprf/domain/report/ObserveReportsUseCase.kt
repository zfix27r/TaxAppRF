package com.taxapprf.domain.report

import com.taxapprf.domain.ReportRepository
import javax.inject.Inject

class ObserveReportsUseCase @Inject constructor(
    private val repository: ReportRepository
) {
    fun execute(observeReportsModel: ObserveReportsModel) =
        repository.observeReports(observeReportsModel)
}