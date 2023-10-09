package com.taxapprf.domain.transactions

import com.taxapprf.domain.TransactionsRepository
import javax.inject.Inject

class DeleteTransactionsUseCase @Inject constructor(
    private val transactionsRepository: TransactionsRepository
) {
    suspend fun execute(transactionsId: List<Int>) =
        transactionsRepository.deleteTransactions(transactionsId)
}