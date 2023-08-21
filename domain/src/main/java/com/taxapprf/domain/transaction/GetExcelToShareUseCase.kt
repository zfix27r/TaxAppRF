package com.taxapprf.domain.transaction

import com.taxapprf.domain.TransactionRepository
import javax.inject.Inject

class GetExcelToShareUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    fun execute(getExcelToShareModel: GetExcelToShareModel) =
        repository.getExcelToShare(getExcelToShareModel)
}