package com.taxapprf.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.taxapprf.data.local.room.entity.LocalCBRCurrencyEntity.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class LocalCBRCurrencyEntity(
    @PrimaryKey
    @ColumnInfo(name = CHAR_CODE)
    val charCode: String,

    @ColumnInfo(name = NAME)
    val name: String,
    @ColumnInfo(name = NUM_CODE)
    val numCode: Int,
) {
    companion object {
        const val TABLE_NAME = "cbr_currency"

        const val CHAR_CODE = "char_code"

        const val NAME = "name"
        const val NUM_CODE = "num_code"
    }
}