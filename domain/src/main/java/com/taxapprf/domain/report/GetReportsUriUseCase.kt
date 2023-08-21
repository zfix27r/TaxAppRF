package com.taxapprf.domain.report

import com.taxapprf.domain.ReportRepository
import com.taxapprf.domain.report.GetReportsUriModel
import javax.inject.Inject

class GetReportsUriUseCase @Inject constructor(
    private val repository: ReportRepository
) {
    fun execute(getReportsUriModel: GetReportsUriModel) =
        repository.getReportsUri(getReportsUriModel)
}