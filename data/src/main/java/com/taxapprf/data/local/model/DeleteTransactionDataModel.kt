package com.taxapprf.data.local.model

import androidx.room.ColumnInfo
import com.taxapprf.data.local.entity.TaxEntity
import com.taxapprf.data.local.entity.TransactionEntity

data class DeleteTransactionDataModel(
    @ColumnInfo(name = TransactionEntity.ACCOUNT)
    val account: String,
    @ColumnInfo(name = TransactionEntity.YEAR)
    val year: String,
)