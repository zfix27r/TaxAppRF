package com.taxapprf.data.local.room.model.tax

import androidx.room.ColumnInfo
import com.taxapprf.data.local.room.LocalDatabase.Companion.CURRENCY_ORDINAL
import com.taxapprf.data.local.room.LocalDatabase.Companion.ID
import com.taxapprf.data.local.room.LocalDatabase.Companion.REPORT_ID
import com.taxapprf.data.local.room.entity.LocalTransactionEntity.Companion.DATE
import com.taxapprf.data.local.room.entity.LocalTransactionEntity.Companion.SUM
import com.taxapprf.data.local.room.entity.LocalTransactionEntity.Companion.SUM_RUB
import com.taxapprf.data.local.room.entity.LocalTransactionEntity.Companion.TAX_RUB

data class TransactionSumRUBDataModel(
    @ColumnInfo(name = ID)
    val id: Int,
    @ColumnInfo(name = REPORT_ID)
    val reportId: Int,
    @ColumnInfo(name = CURRENCY_ORDINAL)
    val currencyOrdinal: Int,
    @ColumnInfo(name = DATE)
    val date: Long,
    @ColumnInfo(name = SUM)
    val sum: Double,
    @ColumnInfo(name = SUM_RUB)
    var sumRUB: Double? = null,
    @ColumnInfo(name = TAX_RUB)
    var taxRUB: Double? = null,
)