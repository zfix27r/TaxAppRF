package com.taxapprf.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.taxapprf.data.local.room.entity.LocalUserEntity.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class LocalUserEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    val id: Int,

    @ColumnInfo(name = EMAIL)
    val email: String?,

    @ColumnInfo(name = AVATAR)
    val avatar: String?,
    @ColumnInfo(name = NAME)
    val name: String?,
    @ColumnInfo(name = PHONE)
    val phone: String?,
) {
    companion object {
        const val TABLE_NAME = "user"

        const val ID = "id"

        const val EMAIL = "email"
        const val AVATAR = "avatar"
        const val NAME = "name"
        const val PHONE = "phone"
    }
}