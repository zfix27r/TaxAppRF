package com.taxapprf.data.local.room.model

import androidx.room.ColumnInfo
import com.taxapprf.data.local.room.entity.LocalCBRCurrencyEntity
import com.taxapprf.data.local.room.entity.LocalCBRRateEntity

data class GetRatesWithCurrency(
    @ColumnInfo(name = LocalCBRCurrencyEntity.NAME)
    val name: String,
    @ColumnInfo(name = LocalCBRCurrencyEntity.CHAR_CODE)
    val charCode: String,
    @ColumnInfo(name = LocalCBRCurrencyEntity.NUM_CODE)
    val numCode: Int,
    @ColumnInfo(name = LocalCBRRateEntity.RATE)
    val rate: Double? = null,
)