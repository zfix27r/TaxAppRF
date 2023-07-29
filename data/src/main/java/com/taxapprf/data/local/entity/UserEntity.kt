package com.taxapprf.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


const val USER_TABLE = "user"

@Entity(tableName = USER_TABLE)
data class UserEntity(
    @ColumnInfo(name = ID)
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = ACTIVE, defaultValue = "0")
    val active: Boolean,

    @ColumnInfo(name = NAME)
    val name: String,

    @ColumnInfo(name = EMAIL)
    val email: String,

    @ColumnInfo(name = PHONE)
    val phone: String,
) {
    companion object {
        const val ID = "id"

        const val NAME = "name"
        const val EMAIL = "email"
        const val PHONE = "phone"

        const val ACTIVE = "active"
    }
}