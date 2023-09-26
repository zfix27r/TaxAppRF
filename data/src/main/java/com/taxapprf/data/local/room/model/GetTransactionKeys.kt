package com.taxapprf.data.local.room.model

import androidx.room.ColumnInfo
import com.taxapprf.data.local.room.entity.LocalTransactionEntity
import com.taxapprf.data.sync.SYNC_AT

data class GetTransactionKeys(
    @ColumnInfo(name = LocalTransactionEntity.ID)
    val id: Int,

    @ColumnInfo(name = LocalTransactionEntity.TAX)
    val tax: Double?,

    @ColumnInfo(name = ACCOUNT_KEY)
    val accountKey: String,
    @ColumnInfo(name = REPORT_KEY)
    val reportKey: String,
    @ColumnInfo(name = TRANSACTION_KEY)
    val transactionKey: String?,

    @ColumnInfo(name = SYNC_AT)
    val syncAt: Long,
) {
    companion object {
        const val ACCOUNT_KEY = "account_key"
        const val REPORT_KEY = "report_key"
        const val TRANSACTION_KEY = "transaction_key"
    }
}