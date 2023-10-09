package com.taxapprf.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.taxapprf.data.local.room.LocalDatabase.Companion.CURRENCY_ORDINAL
import com.taxapprf.data.local.room.entity.LocalCBRRateEntity.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME, primaryKeys = [CURRENCY_ORDINAL, LocalTransactionEntity.DATE])
data class LocalCBRRateEntity(
    @ColumnInfo(name = CURRENCY_ORDINAL)
    val currencyOrdinal: Int,
    @ColumnInfo(name = LocalTransactionEntity.DATE)
    val date: Long,

    @ColumnInfo(name = CURRENCY_RATE)
    val rate: Double? = null,
) {
    companion object {
        const val TABLE_NAME = "cbr_rate"

        const val CURRENCY_RATE = "currency_rate"
    }
}