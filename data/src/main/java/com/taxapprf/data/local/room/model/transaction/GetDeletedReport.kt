package com.taxapprf.data.local.room.model.transaction

import androidx.room.ColumnInfo
import com.taxapprf.data.local.room.LocalDatabase.Companion.ID
import com.taxapprf.data.local.room.entity.LocalReportEntity.Companion.SIZE
import com.taxapprf.data.local.room.entity.LocalReportEntity.Companion.TAX
import com.taxapprf.data.remote.firebase.Firebase.Companion.ACCOUNT_KEY
import com.taxapprf.data.remote.firebase.Firebase.Companion.REPORT_KEY

data class GetDeletedReport(
    @ColumnInfo(name = ID)
    val id: Int,
    @ColumnInfo(name = TAX)
    val tax: Double,
    @ColumnInfo(name = SIZE)
    val size: Int,
    @ColumnInfo(name = ACCOUNT_KEY)
    val accountKey: String,
    @ColumnInfo(name = REPORT_KEY)
    val reportKey: String
)