package com.taxapprf.domain.transaction

import com.taxapprf.domain.FirebaseRequestModel
import com.taxapprf.domain.TransactionRepository
import javax.inject.Inject

class DeleteTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    fun execute(deleteTransactionResponseModel: FirebaseRequestModel) =
        repository.deleteTransaction(deleteTransactionResponseModel)
}