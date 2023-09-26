package com.taxapprf.data.local.room.model

import androidx.room.ColumnInfo
import com.taxapprf.data.local.room.entity.LocalCBRCurrencyEntity

data class GetCachedLocalCBRCurrency(
    @ColumnInfo(name = LocalCBRCurrencyEntity.ID)
    val id: Int,
    @ColumnInfo(name = LocalCBRCurrencyEntity.CHAR_CODE)
    val charCode: String,
)