package com.taxapprf.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


const val TAX_TABLE = "tax"

@Entity(tableName = TAX_TABLE)
data class TaxEntity(
    @ColumnInfo(name = KEY)
    @PrimaryKey
    val key: String,

    @ColumnInfo(name = ACCOUNT)
    val account: String,

    @ColumnInfo(name = YEAR)
    val year: String,

    @ColumnInfo(name = SUM)
    val sum: Double,
) {
    companion object {
        const val KEY = "key"
        const val ACCOUNT = "account"
        const val YEAR = "year"
        const val SUM = "sum"
    }
}