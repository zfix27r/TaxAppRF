package com.taxapprf.domain.transaction

import com.taxapprf.domain.TransactionRepository
import javax.inject.Inject

class ObserveTransactionsUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    fun execute(observeTransactionsModel: ObserveTransactionsModel) =
        repository.observeTransactions(observeTransactionsModel)
}