package com.taxapprf.data.local.room.model

import androidx.room.ColumnInfo
import com.taxapprf.data.local.room.entity.TransactionEntity

data class DeleteTransactionDataModel(
    @ColumnInfo(name = TransactionEntity.ACCOUNT)
    val account: String,
    @ColumnInfo(name = TransactionEntity.YEAR)
    val year: String,
)