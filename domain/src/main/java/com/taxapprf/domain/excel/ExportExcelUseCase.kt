package com.taxapprf.domain.excel

import com.taxapprf.domain.ExcelRepository
import javax.inject.Inject

class ExportExcelUseCase @Inject constructor(
    private val excelRepository: ExcelRepository
) {
    suspend fun execute(exportExcelModel: ExportExcelModel) =
        excelRepository.export(exportExcelModel)
}