package com.taxapprf.data.local.model

import androidx.room.ColumnInfo
import com.taxapprf.data.local.entity.UserEntity

data class UserWithAccountModel(
    @ColumnInfo(name = UserEntity.IS_SIGN_IN)
    val isSignIn: Boolean,

    @ColumnInfo(name = UserEntity.NAME)
    val name: String,

    @ColumnInfo(name = UserEntity.EMAIL)
    val email: String,

    @ColumnInfo(name = UserEntity.PHONE)
    val phone: String,

    @ColumnInfo(name = ACCOUNT_NAME)
    val accountName: String?,

    @ColumnInfo(name = ACCOUNT_ACTIVE)
    val accountActive: Boolean?,
) {
    companion object {
        const val ACCOUNT_NAME = "account_name"
        const val ACCOUNT_ACTIVE = "account_active"
    }
}