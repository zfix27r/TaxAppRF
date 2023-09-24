package com.taxapprf.data.local.room.model

import androidx.room.ColumnInfo
import com.taxapprf.data.local.room.entity.LocalAccountEntity
import com.taxapprf.data.local.room.entity.LocalUserEntity

data class LocalUserWithAccounts(
    @ColumnInfo(name = LocalUserEntity.EMAIL)
    val email: String?,
    @ColumnInfo(name = LocalUserEntity.AVATAR)
    val avatar: String?,
    @ColumnInfo(name = LocalUserEntity.NAME)
    val name: String?,
    @ColumnInfo(name = LocalUserEntity.PHONE)
    val phone: String?,

    @ColumnInfo(name = LocalAccountEntity.ID)
    val accountId: Int,

    @ColumnInfo(name = LocalAccountEntity.USER_ID)
    val userId: Int,

    @ColumnInfo(name = LocalAccountEntity.ACCOUNT_KEY)
    val accountName: String,

    @ColumnInfo(name = LocalAccountEntity.IS_ACTIVE)
    val isActive: Boolean,
)