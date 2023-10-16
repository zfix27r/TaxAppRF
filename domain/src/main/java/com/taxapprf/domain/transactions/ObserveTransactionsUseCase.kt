package com.taxapprf.domain.transactions

import com.taxapprf.domain.TransactionsRepository
import javax.inject.Inject

class ObserveTransactionsUseCase @Inject constructor(
    private val transactionsRepository: TransactionsRepository
) {
    fun execute(reportId: Int) =
        transactionsRepository.observeTransactions(reportId)
}