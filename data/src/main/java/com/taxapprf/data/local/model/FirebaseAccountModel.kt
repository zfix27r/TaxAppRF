package com.taxapprf.data.local.model

import androidx.room.ColumnInfo
import com.taxapprf.data.local.entity.AccountEntity

data class FirebaseAccountModel(
    @ColumnInfo(name = AccountEntity.ID)
    val key: String,
)