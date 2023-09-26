package com.taxapprf.domain.cbr

import com.taxapprf.domain.CBRRepository
import javax.inject.Inject

class GetCurrenciesUseCase @Inject constructor(
    private val repository: CBRRepository
) {
    fun execute() =
        repository.getCurrencies()
}