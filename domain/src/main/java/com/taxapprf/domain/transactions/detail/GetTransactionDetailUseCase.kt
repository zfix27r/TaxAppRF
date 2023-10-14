package com.taxapprf.domain.transactions.detail

import com.taxapprf.domain.TransactionDetailRepository
import javax.inject.Inject

class GetTransactionDetailUseCase @Inject constructor(
    private val transactionDetailRepository: TransactionDetailRepository
) {
    suspend fun execute(getTransactionDetailModel: GetTransactionDetailModel) =
        transactionDetailRepository.getTransactionDetail(getTransactionDetailModel)
}