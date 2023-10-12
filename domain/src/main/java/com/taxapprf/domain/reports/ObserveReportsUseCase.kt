package com.taxapprf.domain.reports

import com.taxapprf.domain.ReportsRepository
import javax.inject.Inject

class ObserveReportsUseCase @Inject constructor(
    private val reportsRepository: ReportsRepository
) {
    fun execute(accountId: Int) =
        reportsRepository.observeAccountReports(accountId)
}