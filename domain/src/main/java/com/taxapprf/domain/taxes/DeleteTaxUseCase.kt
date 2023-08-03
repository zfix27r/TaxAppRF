package com.taxapprf.domain.taxes

import com.taxapprf.domain.TaxRepository
import javax.inject.Inject

class DeleteTaxUseCase @Inject constructor(
    private val repository: TaxRepository
) {
    fun execute(deleteTaxModel: DeleteTaxModel) =
        repository.deleteTax(deleteTaxModel)
}