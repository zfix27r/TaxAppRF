package com.taxapprf.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


const val USER_TABLE = "user"

@Entity(tableName = USER_TABLE)
data class UserEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = ID)
    val id: String,

    @ColumnInfo(name = ACTIVE)
    val active: Boolean,

    @ColumnInfo(name = NAME)
    val name: String,
    @ColumnInfo(name = EMAIL)
    val email: String,

    ) {
    companion object {
        const val ID = "id"

        const val ACTIVE = "active"

        const val NAME = "name"
        const val EMAIL = "email"
    }
}