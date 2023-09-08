package com.taxapprf.domain.report

import com.taxapprf.domain.ReportRepository
import javax.inject.Inject

class ObserveReportUseCase @Inject constructor(
    private val repository: ReportRepository
) {
    fun execute(accountKey: String, reportKey: String) =
        repository.observe(accountKey, reportKey)
}