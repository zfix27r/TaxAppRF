package com.taxapprf.domain.transactions

import com.taxapprf.domain.DeletedRepository
import javax.inject.Inject

class DeleteTransactionsUseCase @Inject constructor(
    private val deletedRepository: DeletedRepository
) {
    suspend fun execute(transactionsId: List<Int>) =
        deletedRepository.deleteTransactions(transactionsId)
}