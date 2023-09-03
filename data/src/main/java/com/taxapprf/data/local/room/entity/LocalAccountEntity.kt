package com.taxapprf.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.taxapprf.data.local.room.entity.LocalAccountEntity.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class LocalAccountEntity(
    @PrimaryKey
    @ColumnInfo(name = ACCOUNT_KEY)
    val accountKey: String,
    @ColumnInfo(name = IS_ACTIVE)
    val isActive: Boolean,
) {
    companion object {
        const val TABLE_NAME = "account"

        const val ACCOUNT_KEY = "account_key"
        const val IS_ACTIVE = "is_active"
    }
}