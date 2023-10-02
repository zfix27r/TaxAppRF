package com.taxapprf.data.local.room.model

import androidx.room.ColumnInfo
import com.taxapprf.data.local.room.entity.LocalTransactionEntity

data class GetTransactionWithCurrency(
    @ColumnInfo(name = LocalTransactionEntity.ID)
    val id: Int,

    @ColumnInfo(name = LocalTransactionEntity.NAME)
    val name: String?,
    @ColumnInfo(name = LocalTransactionEntity.DATE)
    val date: Long,
    @ColumnInfo(name = LocalTransactionEntity.TYPE)
    val type: Int,
    @ColumnInfo(name = LocalTransactionEntity.SUM)
    val sum: Double,
    @ColumnInfo(name = LocalTransactionEntity.TAX)
    val tax: Double?,

    @ColumnInfo(name = CURRENCY_ID)
    val currencyId: Int,
    @ColumnInfo(name = CURRENCY_CHAR_CODE)
    val currencyCharCode: String,
    @ColumnInfo(name = CURRENCY_RATE)
    val currencyRate: Double?,
) {
    companion object {
        const val CURRENCY_ID = "currency_id"
        const val CURRENCY_CHAR_CODE = "currency_char_code"
        const val CURRENCY_RATE = "currency_rate"
    }
}