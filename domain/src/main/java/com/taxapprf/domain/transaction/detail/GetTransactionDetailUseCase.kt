package com.taxapprf.domain.transaction.detail

import com.taxapprf.domain.TransactionDetailRepository
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetTransactionDetailUseCase @Inject constructor(
    private val transactionDetailRepository: TransactionDetailRepository
) {
    fun execute(getTransactionDetailModel: GetTransactionDetailModel) =
        flow {
            emit(transactionDetailRepository.getTransactionDetail(getTransactionDetailModel))
        }
}