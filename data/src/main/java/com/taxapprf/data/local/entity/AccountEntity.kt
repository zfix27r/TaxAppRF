package com.taxapprf.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


const val ACCOUNT_TABLE = "account"
@Entity(tableName = ACCOUNT_TABLE)
data class AccountEntity(
    @ColumnInfo(name = ID)
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = NAME)
    val name: String,

    @ColumnInfo(name = ACTIVE, defaultValue = "0")
    val active: Boolean,
) {
    companion object {
        const val ID = "id"

        const val NAME = "name"

        const val ACTIVE = "active"
    }
}