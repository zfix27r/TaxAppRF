package com.taxapprf.domain.tax

import com.taxapprf.domain.TaxRepository
import javax.inject.Inject

class UpdateAllEmptySumUseCase @Inject constructor(
    private val taxRepository: TaxRepository
) {
    suspend fun execute() =
        taxRepository.updateAllEmptySumRUB()
}