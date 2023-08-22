package com.taxapprf.domain.report

import com.taxapprf.domain.ReportRepository
import javax.inject.Inject

class SaveExcelToFirebaseUseCase @Inject constructor(
    private val repository: ReportRepository
) {
    fun execute(saveReportsFromExcelModel: SaveExcelToFirebaseModel) =
        repository.saveReportsFromExcel(saveReportsFromExcelModel)
}