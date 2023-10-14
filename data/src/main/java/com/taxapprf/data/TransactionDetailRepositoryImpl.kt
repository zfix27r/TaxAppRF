package com.taxapprf.data

import com.taxapprf.data.local.room.LocalTransactionDetailDao
import com.taxapprf.domain.TransactionDetailRepository
import com.taxapprf.domain.transactions.detail.GetTransactionDetailModel
import javax.inject.Inject

class TransactionDetailRepositoryImpl @Inject constructor(
    private val localTransactionDetailDao: LocalTransactionDetailDao
) : TransactionDetailRepository {
    override suspend fun getTransactionDetail(getTransactionDetailModel: GetTransactionDetailModel) =
        localTransactionDetailDao.getTransactionDetail(
            getTransactionDetailModel.reportId,
            getTransactionDetailModel.transactionId
        )
}