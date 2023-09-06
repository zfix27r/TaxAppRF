package com.taxapprf.data.local.room.model

import androidx.room.ColumnInfo
import com.taxapprf.data.local.room.entity.LocalReportEntity

data class LocalDeleteReportModel(
    @ColumnInfo(name = LocalReportEntity.REPORT_KEY)
    val key: String,
    @ColumnInfo(name = LocalReportEntity.ACCOUNT_KEY)
    val accountKey: String,
)