package com.taxapprf.domain.main.transaction

import com.taxapprf.domain.CurrencyRepository
import com.taxapprf.domain.MainRepository
import com.taxapprf.domain.SyncRepository
import javax.inject.Inject

class UpdateTransactionTaxUseCase @Inject constructor(
    private val currencyRepository: CurrencyRepository,
    private val mainRepository: MainRepository,
    private val syncRepository: SyncRepository,
) {
    suspend fun execute(updateTransactionTaxModel: UpdateTransactionTaxModel) {
        currencyRepository.getCurrencyRate(
            updateTransactionTaxModel.currencyOrdinal,
            updateTransactionTaxModel.date
        )?.let {
            updateTransactionTaxModel.rate = it
            mainRepository.updateTransaction(updateTransactionTaxModel)
        }
    }
}