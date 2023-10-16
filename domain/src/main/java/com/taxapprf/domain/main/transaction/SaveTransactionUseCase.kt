package com.taxapprf.domain.main.transaction

import com.taxapprf.domain.MainRepository
import com.taxapprf.domain.tax.UpdateAllEmptySumRUBAndTaxRUBUseCase
import javax.inject.Inject

class SaveTransactionUseCase @Inject constructor(
    private val mainRepository: MainRepository,
    private val updateAllEmptySumRUBAndTaxRUBUseCase: UpdateAllEmptySumRUBAndTaxRUBUseCase,
) {
    suspend fun execute(saveTransactionModel: SaveTransactionModel) {
        mainRepository.saveTransaction(saveTransactionModel)
        updateAllEmptySumRUBAndTaxRUBUseCase.execute()
    }
}