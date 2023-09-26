package com.taxapprf.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.taxapprf.data.getEpochTime
import com.taxapprf.data.local.room.entity.LocalTransactionEntity.Companion.TABLE_NAME
import com.taxapprf.data.sync.REMOTE_KEY
import com.taxapprf.data.sync.SYNC_AT
import com.taxapprf.data.sync.SyncLocal

@Entity(tableName = TABLE_NAME)
data class LocalTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    val id: Int = DEFAULT_ID,

    @ColumnInfo(name = ACCOUNT_ID)
    val accountId: Int,
    @ColumnInfo(name = REPORT_ID)
    val reportId: Int,
    @ColumnInfo(name = CURRENCY_ID)
    val currencyId: Int,

    @ColumnInfo(name = NAME)
    val name: String? = null,
    @ColumnInfo(name = DATE)
    val date: Long,
    @ColumnInfo(name = TYPE)
    val type: Int,
    @ColumnInfo(name = SUM)
    val sum: Double,
    @ColumnInfo(name = TAX)
    val tax: Double? = null,

    @ColumnInfo(name = REMOTE_KEY)
    override val remoteKey: String? = null,
    @ColumnInfo(name = SYNC_AT)
    override val syncAt: Long = getEpochTime(),
) : SyncLocal {
    companion object {
        const val TABLE_NAME = "transaction"

        const val ID = "id"
        const val ACCOUNT_ID = "account_id"
        const val REPORT_ID = "report_id"
        const val CURRENCY_ID = "currency_id"

        const val NAME = "name"
        const val DATE = "date"
        const val TYPE = "type"
        const val SUM = "sum"
        const val TAX = "tax"

        const val DEFAULT_ID = 0
    }
}