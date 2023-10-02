package com.taxapprf.domain.transaction

import com.taxapprf.domain.TransactionRepository
import javax.inject.Inject

class ObserveTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    fun execute(transactionId: Int?) =
        transactionId?.let { repository.observe(transactionId) }
}