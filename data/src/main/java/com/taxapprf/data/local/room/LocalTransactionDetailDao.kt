package com.taxapprf.data.local.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.taxapprf.data.local.room.entity.LocalReportEntity
import com.taxapprf.data.local.room.entity.LocalTransactionEntity
import com.taxapprf.domain.transaction.detail.TransactionDetailModel
import java.time.LocalDate

@Dao
interface LocalTransactionDetailDao {
    @Query("SELECT * FROM report WHERE id = :reportId LIMIT 1")
    fun getLocalReportEntity(reportId: Int): LocalReportEntity?

    @Query("SELECT * FROM `transaction` WHERE id = :transactionId LIMIT 1")
    fun getLocalTransactionEntity(transactionId: Int): LocalTransactionEntity?

    @Transaction
    fun getTransactionDetail(reportId: Int?, transactionId: Int?): TransactionDetailModel? {
        val reportDate = reportId?.let {
            getLocalReportEntity(reportId)?.let {
                val localDate = LocalDate.now()
                localDate.withYear(it.remoteKey.toInt()).toEpochDay()
            }
        }
        val transaction = transactionId?.let { getLocalTransactionEntity(transactionId) }

        return TransactionDetailModel(
            date = transaction?.date ?: reportDate,
            name = transaction?.name,
            transactionTypeOrdinal = transaction?.typeOrdinal,
            currencyOrdinal = transaction?.currencyOrdinal,
            sum = transaction?.sum,
            tax = transaction?.tax
        )
    }
}