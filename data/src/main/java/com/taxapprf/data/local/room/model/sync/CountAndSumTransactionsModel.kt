package com.taxapprf.data.local.room.model.sync

import androidx.room.ColumnInfo
import com.taxapprf.data.local.room.entity.LocalReportEntity.Companion.SIZE
import com.taxapprf.data.local.room.entity.LocalReportEntity.Companion.TAX

data class CountAndSumTransactionsModel(
    @ColumnInfo(name = TAX)
    val tax: Double,
    @ColumnInfo(name = SIZE)
    val size: Int,
)