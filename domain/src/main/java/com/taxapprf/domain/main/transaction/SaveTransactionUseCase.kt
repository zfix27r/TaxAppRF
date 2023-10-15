package com.taxapprf.domain.main.transaction

import com.taxapprf.domain.MainRepository
import com.taxapprf.domain.tax.UpdateAllEmptySumUseCase
import com.taxapprf.domain.tax.UpdateAllEmptyTaxUseCase
import javax.inject.Inject

class SaveTransactionUseCase @Inject constructor(
    private val mainRepository: MainRepository,
    private val updateAllEmptySumUseCase: UpdateAllEmptySumUseCase,
    private val updateAllEmptyTaxUseCase: UpdateAllEmptyTaxUseCase,
) {
    suspend fun execute(saveTransactionModel: SaveTransactionModel) {
        mainRepository.saveTransaction(saveTransactionModel)
        updateAllEmptySumUseCase.execute()
        updateAllEmptyTaxUseCase.execute()
    }
}