package com.taxapprf.data.local.room.model

import androidx.room.ColumnInfo
import com.taxapprf.data.local.room.entity.UserEntity

data class UserWithAccountDataModel(
    @ColumnInfo(name = UserEntity.IS_SIGN_IN)
    val isSignIn: Boolean,

    @ColumnInfo(name = UserEntity.NAME)
    val name: String,

    @ColumnInfo(name = UserEntity.EMAIL)
    val email: String,

    @ColumnInfo(name = UserEntity.PHONE)
    val phone: String,

    @ColumnInfo(name = ACCOUNT)
    val account: String?,

    @ColumnInfo(name = ACCOUNT_ACTIVE)
    val accountActive: Boolean?,
) {
    companion object {
        const val ACCOUNT = "account"
        const val ACCOUNT_ACTIVE = "account_active"
    }
}