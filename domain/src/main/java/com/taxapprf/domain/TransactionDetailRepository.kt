package com.taxapprf.domain

import com.taxapprf.domain.transactions.detail.GetTransactionDetailModel
import com.taxapprf.domain.transactions.detail.TransactionDetailModel

interface TransactionDetailRepository {
    suspend fun getTransactionDetail(getTransactionDetailModel: GetTransactionDetailModel): TransactionDetailModel?
}