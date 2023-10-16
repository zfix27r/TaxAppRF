package com.taxapprf.data.local.room.model.excel

import androidx.room.ColumnInfo
import com.taxapprf.data.local.room.LocalDatabase.Companion.CURRENCY_ORDINAL
import com.taxapprf.data.local.room.LocalDatabase.Companion.TYPE_ORDINAL
import com.taxapprf.data.local.room.entity.LocalCurrencyRateEntity.Companion.CURRENCY_RATE
import com.taxapprf.data.local.room.entity.LocalTransactionEntity.Companion.DATE
import com.taxapprf.data.local.room.entity.LocalTransactionEntity.Companion.NAME
import com.taxapprf.data.local.room.entity.LocalTransactionEntity.Companion.SUM
import com.taxapprf.data.local.room.entity.LocalTransactionEntity.Companion.TAX_RUB

data class ExcelTransactionDataModel(
    @ColumnInfo(name = NAME)
    val name: String?,
    @ColumnInfo(name = DATE)
    val date: Long,
    @ColumnInfo(name = TYPE_ORDINAL)
    val typeOrdinal: Int,
    @ColumnInfo(name = CURRENCY_ORDINAL)
    val currencyOrdinal: Int,
    @ColumnInfo(name = SUM)
    val sum: Double,
    @ColumnInfo(name = TAX_RUB)
    val taxRUB: Double?,
    @ColumnInfo(name = CURRENCY_RATE)
    val currencyRate: Double?,
)