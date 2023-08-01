package com.taxapprf.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


const val TAXES_TABLE = "taxes"

@Entity(tableName = TAXES_TABLE)
data class TaxesEntity(
    @ColumnInfo(name = ID)
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = ACCOUNT)
    val account: String,

    @ColumnInfo(name = YEAR)
    val year: String,
    @ColumnInfo(name = SUM_TAXES)
    val sumTaxes: Long,
) {
    companion object {
        const val ID = "id"

        const val ACCOUNT = "account"

        const val YEAR = "year"
        const val SUM_TAXES = "sumTaxes"
    }
}