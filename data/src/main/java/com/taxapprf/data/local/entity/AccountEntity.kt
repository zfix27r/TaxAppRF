package com.taxapprf.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


const val ACCOUNT_TABLE = "account"

@Entity(tableName = ACCOUNT_TABLE)
data class AccountEntity(
    @ColumnInfo(name = NAME)
    @PrimaryKey
    val name: String,

    @ColumnInfo(name = USER)
    val user: String,

    @ColumnInfo(name = ACTIVE, defaultValue = "0")
    val active: Boolean,
) {
    companion object {
        const val USER = "user"
        const val NAME = "name"
        const val ACTIVE = "active"
    }
}