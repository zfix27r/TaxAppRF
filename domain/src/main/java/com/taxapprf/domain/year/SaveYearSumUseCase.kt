package com.taxapprf.domain.year

import com.taxapprf.domain.TransactionRepository
import com.taxapprf.domain.year.SaveYearSumModel
import javax.inject.Inject

class SaveYearSumUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    fun execute(saveYearSumModel: SaveYearSumModel) = repository.saveYearSum(saveYearSumModel)
}