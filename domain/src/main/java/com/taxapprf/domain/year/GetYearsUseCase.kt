package com.taxapprf.domain.year

import com.taxapprf.domain.FirebaseRequestModel
import com.taxapprf.domain.YearRepository
import javax.inject.Inject

class GetYearsUseCase @Inject constructor(
    private val repository: YearRepository
) {
    fun execute(requestModel: FirebaseRequestModel) = repository.getYears(requestModel)
}