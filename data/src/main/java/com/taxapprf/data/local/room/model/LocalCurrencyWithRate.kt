package com.taxapprf.data.local.room.model

import androidx.room.ColumnInfo
import com.taxapprf.data.local.room.entity.LocalAccountEntity
import com.taxapprf.data.local.room.entity.LocalCBRCurrencyEntity
import com.taxapprf.data.local.room.entity.LocalCBRRateEntity
import com.taxapprf.data.local.room.entity.LocalUserEntity
import com.taxapprf.data.sync.REMOTE_KEY

data class LocalCurrencyWithRate(
    @ColumnInfo(name = LocalCBRCurrencyEntity.NAME)
    val name: String,
    @ColumnInfo(name = LocalCBRCurrencyEntity.CHAR_CODE)
    val charCode: String,
    @ColumnInfo(name = LocalCBRCurrencyEntity.NUM_CODE)
    val numCode: Int,

    @ColumnInfo(name = LocalCBRRateEntity.DATE)
    val date: Long,
    @ColumnInfo(name = LocalCBRRateEntity.RATE)
    val rate: Double?,
)