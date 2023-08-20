package com.taxapprf.domain.report

import com.taxapprf.domain.ReportRepository
import javax.inject.Inject

class GetReportsUseCase @Inject constructor(
    private val repository: ReportRepository
) {
    fun execute(getReportsModel: GetReportsModel) =
        repository.getReports(getReportsModel)
}