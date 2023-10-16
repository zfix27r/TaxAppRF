package com.taxapprf.data.local.room.model.transaction

import androidx.room.ColumnInfo
import com.taxapprf.data.local.room.LocalDatabase.Companion.REPORT_ID
import com.taxapprf.data.local.room.LocalDatabase.Companion.TRANSACTION_ID
import com.taxapprf.data.local.room.entity.LocalTransactionEntity.Companion.TAX_RUB
import com.taxapprf.data.sync.REMOTE_KEY
import com.taxapprf.data.sync.SYNC_AT

data class DeletedTransactionDataModel(
    @ColumnInfo(name = TRANSACTION_ID)
    val id: Int,
    @ColumnInfo(name = REPORT_ID)
    val reportId: Int,
    @ColumnInfo(name = TAX_RUB)
    val taxRUB: Double?,
    @ColumnInfo(name = REMOTE_KEY)
    val remoteKey: String?,
    @ColumnInfo(name = SYNC_AT)
    val syncAt: Long,
)