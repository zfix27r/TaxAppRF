package com.taxapprf.data.local.room.model

import androidx.room.ColumnInfo
import com.taxapprf.data.local.room.entity.LocalTransactionEntity

data class LocalDeleteTransactionModel(
    @ColumnInfo(name = LocalTransactionEntity.ACCOUNT_KEY)
    val accountKey: String,
    @ColumnInfo(name = LocalTransactionEntity.REPORT_KEY)
    val yearKey: String,
)