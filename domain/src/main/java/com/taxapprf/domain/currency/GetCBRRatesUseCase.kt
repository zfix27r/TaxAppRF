package com.taxapprf.domain.currency

import com.taxapprf.domain.CBRRepository
import javax.inject.Inject

class GetCBRRatesUseCase @Inject constructor(
    private val repository: CBRRepository
) {
    suspend fun execute(date: Long) =
        repository.getCurrenciesWithRate(date)
}