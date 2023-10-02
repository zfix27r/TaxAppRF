package com.taxapprf.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.taxapprf.data.local.room.entity.LocalCBRRateEntity.Companion.CURRENCY_ID
import com.taxapprf.data.local.room.entity.LocalCBRRateEntity.Companion.DATE
import com.taxapprf.data.local.room.entity.LocalCBRRateEntity.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME, primaryKeys = [CURRENCY_ID, DATE])
data class LocalCBRRateEntity(
    @ColumnInfo(name = CURRENCY_ID)
    val currencyId: Int,

    @ColumnInfo(name = DATE)
    val date: Long,
    @ColumnInfo(name = RATE)
    val rate: Double? = null,
) {
    companion object {
        const val TABLE_NAME = "cbr_rate"

        const val CURRENCY_ID = "currency_id"

        const val DATE = "date"
        const val RATE = "rate"
    }
}