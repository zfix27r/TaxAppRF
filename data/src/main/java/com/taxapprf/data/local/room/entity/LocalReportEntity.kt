package com.taxapprf.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.taxapprf.data.local.room.entity.LocalReportEntity.Companion.ACCOUNT_KEY
import com.taxapprf.data.local.room.entity.LocalReportEntity.Companion.TABLE_NAME
import com.taxapprf.data.local.room.entity.LocalReportEntity.Companion.YEAR_KEY
import com.taxapprf.domain.Sync

@Entity(
    tableName = TABLE_NAME,
    primaryKeys = [ACCOUNT_KEY, YEAR_KEY]
)
data class LocalReportEntity(
    @ColumnInfo(name = YEAR_KEY)
    override val key: String,
    @ColumnInfo(name = ACCOUNT_KEY)
    val accountKey: String,
    @ColumnInfo(name = TAX)
    val tax: Double,
    @ColumnInfo(name = SIZE)
    val size: Int,

    @ColumnInfo(name = IS_SYNC)
    override val isSync: Boolean,
    @ColumnInfo(name = SYNC_AT)
    override val syncAt: Long,
) : Sync {
    companion object {
        const val TABLE_NAME = "report"

        const val ACCOUNT_KEY = "account_key"

        const val YEAR_KEY = "year_key"
        const val TAX = "tax"
        const val SIZE = "size"

        const val IS_SYNC = "is_sync"
        const val SYNC_AT = "sync_at"
    }
}