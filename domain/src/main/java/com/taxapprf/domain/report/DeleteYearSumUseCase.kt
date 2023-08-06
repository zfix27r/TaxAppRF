package com.taxapprf.domain.report

import com.taxapprf.domain.FirebaseRequestModel
import com.taxapprf.domain.TransactionRepository
import javax.inject.Inject

class DeleteYearSumUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    fun execute(requestModel: FirebaseRequestModel) = repository.getYearSum(requestModel)
}