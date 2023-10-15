package com.taxapprf.data.local.room.model.transaction

import androidx.room.ColumnInfo
import com.taxapprf.data.local.room.LocalDatabase.Companion.ID
import com.taxapprf.data.local.room.entity.LocalReportEntity.Companion.SIZE
import com.taxapprf.data.local.room.entity.LocalReportEntity.Companion.TAX_RUB
import com.taxapprf.data.remote.firebase.Firebase.Companion.ACCOUNT_KEY
import com.taxapprf.data.remote.firebase.Firebase.Companion.REPORT_KEY

data class DeletedReportDataModel(
    @ColumnInfo(name = ID)
    val id: Int,
    @ColumnInfo(name = TAX_RUB)
    val taxRUB: Double,
    @ColumnInfo(name = SIZE)
    val size: Int,
    @ColumnInfo(name = ACCOUNT_KEY)
    val accountKey: String,
    @ColumnInfo(name = REPORT_KEY)
    val reportKey: String
)