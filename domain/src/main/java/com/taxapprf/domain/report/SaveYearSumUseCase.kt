package com.taxapprf.domain.report

import com.taxapprf.domain.TransactionRepository
import javax.inject.Inject

class SaveYearSumUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    fun execute(saveYearSumModel: SaveYearSumModel) = repository.saveYearSum(saveYearSumModel)
}