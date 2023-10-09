package com.taxapprf.data.local.room.model.sync

import androidx.room.ColumnInfo
import com.taxapprf.data.local.room.LocalDatabase.Companion.ID
import com.taxapprf.data.remote.firebase.Firebase.Companion.ACCOUNT_KEY
import com.taxapprf.data.remote.firebase.Firebase.Companion.REPORT_KEY

data class GetSyncResultReportModel(
    @ColumnInfo(name = ID)
    val reportId: Int,
    @ColumnInfo(name = ACCOUNT_KEY)
    val accountKey: String,
    @ColumnInfo(name = REPORT_KEY)
    val reportKey: String,
)