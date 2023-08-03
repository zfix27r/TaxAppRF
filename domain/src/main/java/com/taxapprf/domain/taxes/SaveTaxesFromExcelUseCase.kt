package com.taxapprf.domain.taxes

import com.taxapprf.domain.TaxRepository
import javax.inject.Inject

class SaveTaxesFromExcelUseCase @Inject constructor(
    private val repository: TaxRepository
) {
    fun execute(storagePath: String) =
        repository.saveTaxesFromExcel(storagePath)
}