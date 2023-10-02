package com.taxapprf.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.taxapprf.data.local.room.entity.LocalReportEntity.Companion.TABLE_NAME
import com.taxapprf.data.sync.DEFAULT_KEY
import com.taxapprf.data.sync.DEFAULT_SYNC_AT
import com.taxapprf.data.sync.REMOTE_KEY
import com.taxapprf.data.sync.SYNC_AT
import com.taxapprf.data.sync.SyncLocal

@Entity(tableName = TABLE_NAME)
data class LocalReportEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    val id: Int = DEFAULT_ID,

    @ColumnInfo(name = ACCOUNT_ID)
    val accountId: Int,

    @ColumnInfo(name = TAX)
    val tax: Double,
    @ColumnInfo(name = SIZE)
    val size: Int,

    @ColumnInfo(name = REMOTE_KEY)
    override val remoteKey: String = DEFAULT_KEY,
    @ColumnInfo(name = SYNC_AT)
    override val syncAt: Long = DEFAULT_SYNC_AT,
) : SyncLocal {
    companion object {
        const val TABLE_NAME = "report"

        const val ID = "id"
        const val ACCOUNT_ID = "account_id"

        const val TAX = "tax"
        const val SIZE = "size"

        const val DEFAULT_ID = 0
        const val DEFAULT_TAX = 0.0
        const val DEFAULT_SIZE = 1
    }
}