package com.taxapprf.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


const val TRANSACTION_TABLE = "transaction"

@Entity(tableName = TRANSACTION_TABLE)
data class TransactionEntity(
    @ColumnInfo(name = KEY)
    @PrimaryKey
    val key: String,

    @ColumnInfo(name = ACCOUNT)
    val account: String,

    @ColumnInfo(name = YEAR)
    val year: String,

    @ColumnInfo(name = TYPE)
    val type: String,
    @ColumnInfo(name = ID)
    val id: String,
    @ColumnInfo(name = DATE)
    val date: String,
    @ColumnInfo(name = CURRENCY)
    val currency: String,
    @ColumnInfo(name = RATE_CENTRAL_BANK)
    val rateCentralBank: Double,
    @ColumnInfo(name = SUM)
    val sum: Double,
    @ColumnInfo(name = SUM_RUB)
    val sumRub: Double,
) {
    companion object {
        const val KEY = "key"

        const val ACCOUNT = "account"
        const val YEAR = "year"

        const val TYPE = "type"
        const val ID = "id"
        const val DATE = "date"
        const val CURRENCY = "currency"
        const val RATE_CENTRAL_BANK = "rate_central_bank"
        const val SUM = "sum"
        const val SUM_RUB = "sum_rub"
    }
}