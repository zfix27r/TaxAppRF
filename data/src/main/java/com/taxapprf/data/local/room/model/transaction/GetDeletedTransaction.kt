package com.taxapprf.data.local.room.model.transaction

import androidx.room.ColumnInfo
import com.taxapprf.data.local.room.LocalDatabase
import com.taxapprf.data.local.room.entity.LocalTransactionEntity
import com.taxapprf.data.sync.REMOTE_KEY
import com.taxapprf.data.sync.SYNC_AT

data class GetDeletedTransaction(
    @ColumnInfo(name = LocalDatabase.TRANSACTION_ID)
    val id: Int,
    @ColumnInfo(name = LocalDatabase.REPORT_ID)
    val reportId: Int,
    @ColumnInfo(name = LocalTransactionEntity.TAX)
    val tax: Double?,
    @ColumnInfo(name = REMOTE_KEY)
    val remoteKey: String?,
    @ColumnInfo(name = SYNC_AT)
    val syncAt: Long,
)