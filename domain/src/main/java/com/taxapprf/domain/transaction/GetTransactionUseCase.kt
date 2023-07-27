package com.taxapprf.domain.transaction

import com.taxapprf.domain.FirebaseRequestModel
import com.taxapprf.domain.TransactionRepository
import javax.inject.Inject

class GetTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    fun execute(requestModel: FirebaseRequestModel) =
        repository.getTransactionModel(requestModel)
}