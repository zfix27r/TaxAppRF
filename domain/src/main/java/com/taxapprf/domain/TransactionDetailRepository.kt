package com.taxapprf.domain

import com.taxapprf.domain.transaction.detail.GetTransactionDetailModel
import com.taxapprf.domain.transaction.detail.TransactionDetailModel

interface TransactionDetailRepository {
    fun getTransactionDetail(getTransactionDetailModel: GetTransactionDetailModel): TransactionDetailModel?
}