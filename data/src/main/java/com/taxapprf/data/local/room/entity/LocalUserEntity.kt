package com.taxapprf.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.taxapprf.data.local.room.LocalDatabase.Companion.DEFAULT_ID
import com.taxapprf.data.local.room.LocalDatabase.Companion.ID
import com.taxapprf.data.local.room.entity.LocalUserEntity.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class LocalUserEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    val id: Int = DEFAULT_ID,

    @ColumnInfo(name = EMAIL)
    val email: String? = null,

    @ColumnInfo(name = AVATAR)
    val avatar: String? = null,
    @ColumnInfo(name = NAME)
    val name: String? = null,
    @ColumnInfo(name = PHONE)
    val phone: String? = null,
) {
    companion object {
        const val TABLE_NAME = "user"

        const val EMAIL = "email"
        const val AVATAR = "avatar"
        const val NAME = "name"
        const val PHONE = "phone"
    }
}