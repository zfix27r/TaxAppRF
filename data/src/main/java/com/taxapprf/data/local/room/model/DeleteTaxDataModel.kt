package com.taxapprf.data.local.room.model

import androidx.room.ColumnInfo
import com.taxapprf.data.local.room.entity.TaxEntity

data class DeleteTaxDataModel(
    @ColumnInfo(name = TaxEntity.ACCOUNT)
    val account: String,
    @ColumnInfo(name = TaxEntity.YEAR)
    val year: String,
)