package com.taxapprf.domain.report

import com.taxapprf.domain.ReportRepository
import javax.inject.Inject

class DeleteReportWithTransactionsUseCase @Inject constructor(
    private val repository: ReportRepository
) {
    suspend fun execute(deleteReportWithTransactionsModel: DeleteWithTransactionsModel) =
        repository.deleteWithTransactions(deleteReportWithTransactionsModel)
}