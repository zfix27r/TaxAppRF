package com.taxapprf.domain.excel

import com.taxapprf.domain.ExcelRepository
import com.taxapprf.domain.transaction.SaveTransactionUseCase
import javax.inject.Inject

class ImportExcelUseCase @Inject constructor(
    private val excelRepository: ExcelRepository,
    private val saveTransactionUseCase: SaveTransactionUseCase,
) {
    suspend fun execute(importExcelModel: ImportExcelModel) {
        excelRepository.import(importExcelModel).map {
            saveTransactionUseCase.execute(it)
        }
    }
}