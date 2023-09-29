package com.taxapprf.domain.transaction

import com.taxapprf.domain.TransactionRepository
import javax.inject.Inject

class GetTransactionTypesUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    fun execute() =
        repository.getTransactionTypes()
}