package com.taxapprf.domain.transaction

import com.taxapprf.domain.TransactionRepository
import javax.inject.Inject

class SaveTransactionsFromExcelUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    fun execute(saveReportsFromExcelModel: SaveTransactionsFromExcelModel) =
        repository.saveFromExcel(saveReportsFromExcelModel)
}