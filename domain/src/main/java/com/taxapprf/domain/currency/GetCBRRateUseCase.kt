package com.taxapprf.domain.currency

import com.taxapprf.domain.CBRRepository
import javax.inject.Inject

class GetCBRRateUseCase @Inject constructor(
    private val repository: CBRRepository
) {
    suspend fun execute(date: Long, currencyCharCode: String) =
        repository.getRate(date, currencyCharCode)
}