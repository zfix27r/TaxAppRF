package com.taxapprf.domain.cbr

import com.taxapprf.domain.ReportRepository
import javax.inject.Inject

class GetRateCentralBankUseCase @Inject constructor(
    private val repository: ReportRepository
) {
//    fun execute(date: String, currency: String) = repository.getCBRRate(date, currency)
}