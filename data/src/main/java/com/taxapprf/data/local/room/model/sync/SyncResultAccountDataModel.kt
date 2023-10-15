package com.taxapprf.data.local.room.model.sync

import androidx.room.ColumnInfo
import com.taxapprf.data.local.room.LocalDatabase.Companion.ID
import com.taxapprf.data.remote.firebase.Firebase.Companion.ACCOUNT_KEY

data class SyncResultAccountDataModel(
    @ColumnInfo(name = ID)
    val accountId: Int,
    @ColumnInfo(name = ACCOUNT_KEY)
    val accountKey: String?,
)