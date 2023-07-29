package com.taxapprf.domain.taxes

import com.taxapprf.domain.TaxesRepository
import javax.inject.Inject

class GetTaxesUseCase @Inject constructor(
    private val repository: TaxesRepository
) {
    fun execute() = repository.getTaxes()
}