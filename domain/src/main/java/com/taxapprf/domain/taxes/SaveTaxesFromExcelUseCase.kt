package com.taxapprf.domain.taxes

import com.taxapprf.domain.ReportRepository
import javax.inject.Inject

class SaveTaxesFromExcelUseCase @Inject constructor(
    private val repository: ReportRepository
) {
    fun execute(storagePath: String) =
        repository.saveReportFromExcel(storagePath)
}