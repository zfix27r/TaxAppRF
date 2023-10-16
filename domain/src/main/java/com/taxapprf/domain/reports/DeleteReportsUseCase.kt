package com.taxapprf.domain.reports

import com.taxapprf.domain.DeletedRepository
import com.taxapprf.domain.SyncRepository
import javax.inject.Inject

class DeleteReportsUseCase @Inject constructor(
    private val deletedRepository: DeletedRepository,
    private val syncRepository: SyncRepository,
) {
    suspend fun execute(reportIds: List<Int>) {
        deletedRepository.deleteReports(reportIds)
        syncRepository.syncDeleted()
    }
}