package com.taxapprf.data.local.room.model

import androidx.room.ColumnInfo
import com.taxapprf.data.local.room.entity.LocalAccountEntity

data class LocalAccountSwitchModel(
    @ColumnInfo(name = LocalAccountEntity.ID)
    val id: Int,

    @ColumnInfo(name = LocalAccountEntity.IS_ACTIVE)
    val isActive: Boolean,
)