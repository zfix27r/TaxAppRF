package com.taxapprf.domain.transaction

import com.taxapprf.domain.TransactionRepository
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    fun execute(getTransactionsModel: GetTransactionsModel) =
        repository.getTransactions(getTransactionsModel)
}