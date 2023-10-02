package com.taxapprf.data.local.room.model

import androidx.room.ColumnInfo
import com.taxapprf.data.REPORT_ID
import com.taxapprf.data.TRANSACTION_ID
import com.taxapprf.data.local.room.entity.LocalTransactionEntity
import com.taxapprf.data.sync.ACCOUNT_KEY
import com.taxapprf.data.sync.REPORT_KEY
import com.taxapprf.data.sync.SYNC_AT
import com.taxapprf.data.sync.TRANSACTION_KEY

data class GetTransactionKeys(
    @ColumnInfo(name = TRANSACTION_ID)
    val id: Int,

    @ColumnInfo(name = REPORT_ID)
    val reportId: Int?,

    @ColumnInfo(name = LocalTransactionEntity.TAX)
    val tax: Double?,

    @ColumnInfo(name = ACCOUNT_KEY)
    val accountKey: String,
    @ColumnInfo(name = REPORT_KEY)
    val reportKey: String?,
    @ColumnInfo(name = TRANSACTION_KEY)
    val transactionKey: String?,

    @ColumnInfo(name = SYNC_AT)
    val syncAt: Long,
)