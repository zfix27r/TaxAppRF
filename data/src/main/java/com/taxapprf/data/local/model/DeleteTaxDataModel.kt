package com.taxapprf.data.local.model

import androidx.room.ColumnInfo
import com.taxapprf.data.local.entity.TaxEntity

data class DeleteTaxDataModel(
    @ColumnInfo(name = TaxEntity.ACCOUNT)
    val account: String,
    @ColumnInfo(name = TaxEntity.YEAR)
    val year: String,
)