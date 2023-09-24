package com.taxapprf.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.taxapprf.data.local.room.entity.LocalCBRRateEntity.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class LocalCBRRateEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    val id: Int,

    @ColumnInfo(name = CURRENCY_ID)
    val currencyId: Int,

    @ColumnInfo(name = DATE)
    val date: Long,
    @ColumnInfo(name = RATE)
    val rate: Long,
    @ColumnInfo(name = UNIT)
    val unit: Int,
) {
    companion object {
        const val TABLE_NAME = "cbr_rate"

        const val ID = "id"
        const val CURRENCY_ID = "currency_id"

        const val DATE = "date"
        const val RATE = "rate"
        const val UNIT = "unit"
    }
}