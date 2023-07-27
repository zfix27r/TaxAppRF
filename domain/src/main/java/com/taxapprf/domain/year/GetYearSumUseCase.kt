package com.taxapprf.domain.year

import com.taxapprf.domain.FirebaseRequestModel
import com.taxapprf.domain.TransactionRepository
import javax.inject.Inject

class GetYearSumUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    fun execute(requestModel: FirebaseRequestModel) =
        repository.getYearSum(requestModel)
}