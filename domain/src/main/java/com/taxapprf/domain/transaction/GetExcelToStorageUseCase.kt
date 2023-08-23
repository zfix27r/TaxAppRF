package com.taxapprf.domain.transaction

import com.taxapprf.domain.TransactionRepository
import javax.inject.Inject

class GetExcelToStorageUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    fun execute(getExcelToStorageModel: GetExcelToStorageModel) =
        repository.getExcelToStorage(getExcelToStorageModel)
}