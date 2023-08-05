package com.taxapprf.domain.cbr

import com.taxapprf.domain.TaxRepository
import javax.inject.Inject

class GetRateCentralBankUseCase @Inject constructor(
    private val repository: TaxRepository
) {
    fun execute(date: String, currency: String) = repository.getCBRRate(date, currency)
}