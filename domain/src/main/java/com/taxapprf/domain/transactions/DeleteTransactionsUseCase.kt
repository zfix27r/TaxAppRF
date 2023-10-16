package com.taxapprf.domain.transactions

import com.taxapprf.domain.DeletedRepository
import com.taxapprf.domain.SyncRepository
import javax.inject.Inject

class DeleteTransactionsUseCase @Inject constructor(
    private val deletedRepository: DeletedRepository,
    private val syncRepository: SyncRepository,
) {
    suspend fun execute(transactionsId: List<Int>) {
        deletedRepository.deleteTransactions(transactionsId)
        syncRepository.syncDeleted()
    }
}