package com.taxapprf.data.local.room.model

import androidx.room.ColumnInfo
import com.taxapprf.data.local.room.LocalDatabase.Companion.CURRENCY_ORDINAL
import com.taxapprf.data.local.room.LocalDatabase.Companion.TRANSACTION_ID
import com.taxapprf.data.local.room.LocalDatabase.Companion.TYPE_ORDINAL
import com.taxapprf.data.local.room.entity.LocalCBRRateEntity.Companion.CURRENCY_RATE
import com.taxapprf.data.local.room.entity.LocalTransactionEntity.Companion.DATE
import com.taxapprf.data.local.room.entity.LocalTransactionEntity.Companion.NAME
import com.taxapprf.data.local.room.entity.LocalTransactionEntity.Companion.SUM
import com.taxapprf.data.local.room.entity.LocalTransactionEntity.Companion.TAX

data class GetTransaction(
    @ColumnInfo(name = TRANSACTION_ID)
    val id: Int,

    @ColumnInfo(name = NAME)
    val name: String?,
    @ColumnInfo(name = DATE)
    val date: Long,
    @ColumnInfo(name = SUM)
    val sum: Double,
    @ColumnInfo(name = TAX)
    val tax: Double?,

    @ColumnInfo(name = TYPE_ORDINAL)
    val typeOrdinal: Int,

    @ColumnInfo(name = CURRENCY_ORDINAL)
    val currencyOrdinal: Int,
    @ColumnInfo(name = CURRENCY_RATE)
    val currencyRate: Double?,
)