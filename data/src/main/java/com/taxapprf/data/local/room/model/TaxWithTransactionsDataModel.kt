package com.taxapprf.data.local.room.model

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.taxapprf.data.local.room.entity.TransactionEntity

data class TaxWithTransactionsDataModel(
    @ColumnInfo(name = TAX_SUM)
    val taxSum: Double,

    @ColumnInfo(name = TransactionEntity.KEY)
    @PrimaryKey val key: String,

    @ColumnInfo(name = TransactionEntity.TYPE)
    val type: String,
    @ColumnInfo(name = TransactionEntity.ID)
    val id: String,
    @ColumnInfo(name = TransactionEntity.DATE)
    val date: String,
    @ColumnInfo(name = TransactionEntity.CURRENCY)
    val currency: String,
    @ColumnInfo(name = TransactionEntity.RATE_CENTRAL_BANK)
    val rateCentralBank: Double,
    @ColumnInfo(name = TransactionEntity.SUM)
    val sum: Double,
    @ColumnInfo(name = TransactionEntity.SUM_RUB)
    val sumRub: Double,
) {
    companion object {
        const val TAX_SUM = "tax_sum"
    }
}