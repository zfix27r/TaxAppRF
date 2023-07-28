package com.taxapprf.domain.cbr

import com.taxapprf.domain.TaxesRepository
import javax.inject.Inject

class GetRateCentralBankUseCase @Inject constructor(
    private val repository: TaxesRepository
) {
    fun execute(date: String, currency: String) = repository.getCBRRate(date, currency)
}