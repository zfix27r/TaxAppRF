package com.taxapprf.domain.cbr

import com.taxapprf.domain.CentralBankRepository
import javax.inject.Inject

class GetRateCentralBankUseCase @Inject constructor(
    private val repository: CentralBankRepository
) {
    fun execute(date: String, currency: String) = repository.getRate(date, currency)
}