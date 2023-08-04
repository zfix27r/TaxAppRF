package com.taxapprf.domain.transaction

import com.taxapprf.domain.FirebaseRequestModel
import com.taxapprf.domain.TransactionRepository
import javax.inject.Inject

class SaveTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    fun execute(request: FirebaseRequestModel, transaction: SaveTransactionModel) =
        repository.saveTransactionModel(request, transaction)
}