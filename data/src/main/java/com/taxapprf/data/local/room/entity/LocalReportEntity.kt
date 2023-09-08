package com.taxapprf.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.taxapprf.data.local.room.entity.LocalReportEntity.Companion.TABLE_NAME
import com.taxapprf.data.sync.IS_SYNC
import com.taxapprf.data.sync.IS_DELETE
import com.taxapprf.data.sync.SYNC_AT
import com.taxapprf.domain.Sync

@Entity(tableName = TABLE_NAME)
data class LocalReportEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    val id: Int,

    @ColumnInfo(name = ACCOUNT_KEY)
    val accountKey: String,
    @ColumnInfo(name = REPORT_KEY)
    override val key: String,

    @ColumnInfo(name = TAX)
    val tax: Double,
    @ColumnInfo(name = SIZE)
    val size: Int,

    @ColumnInfo(name = IS_SYNC)
    override val isSync: Boolean,
    @ColumnInfo(name = IS_DELETE)
    override val isDelete: Boolean,
    @ColumnInfo(name = SYNC_AT)
    override val syncAt: Long,
) : Sync {
    companion object {
        const val TABLE_NAME = "report"

        const val ID = "id"

        const val ACCOUNT_KEY = "account_key"
        const val REPORT_KEY = "report_key"

        const val TAX = "tax"
        const val SIZE = "size"
    }
}