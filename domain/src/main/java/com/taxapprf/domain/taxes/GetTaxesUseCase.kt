package com.taxapprf.domain.taxes

import com.taxapprf.domain.TaxRepository
import javax.inject.Inject

class GetTaxesUseCase @Inject constructor(
    private val repository: TaxRepository
) {
    fun execute(accountName: String) = repository.getTaxes(accountName)
}