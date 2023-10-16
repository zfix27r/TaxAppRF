package com.taxapprf.domain.transactions

import com.taxapprf.domain.TransactionsRepository
import javax.inject.Inject

class ObserveReportUseCase @Inject constructor(
    private val transactionsRepository: TransactionsRepository
) {
    fun execute(reportId: Int) =
        transactionsRepository.observeReport(reportId)
}