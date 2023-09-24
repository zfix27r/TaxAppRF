package com.taxapprf.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.taxapprf.data.local.room.entity.LocalCBRRateEntity.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class LocalCBRRateEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    val id: Int = 0,

    @ColumnInfo(name = CHAR_CODE)
    val charCode: String,

    @ColumnInfo(name = DATE)
    val date: Long,
    @ColumnInfo(name = RATE)
    val rate: Double,
) {
    companion object {
        const val TABLE_NAME = "cbr_rate"

        const val ID = "id"

        const val CHAR_CODE = "char_code"

        const val DATE = "date"
        const val RATE = "rate"
    }
}