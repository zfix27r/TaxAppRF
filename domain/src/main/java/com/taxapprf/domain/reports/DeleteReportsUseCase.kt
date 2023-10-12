package com.taxapprf.domain.reports

import com.taxapprf.domain.DeletedRepository
import javax.inject.Inject

class DeleteReportsUseCase @Inject constructor(
    private val deletedRepository: DeletedRepository
) {
    suspend fun execute(reportIds: List<Int>) =
        deletedRepository.deleteReports(reportIds)
}