package com.taxapprf.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.taxapprf.data.local.room.entity.LocalTransactionEntity.Companion.ACCOUNT_KEY
import com.taxapprf.data.local.room.entity.LocalTransactionEntity.Companion.TABLE_NAME
import com.taxapprf.data.local.room.entity.LocalTransactionEntity.Companion.TRANSACTION_KEY
import com.taxapprf.data.local.room.entity.LocalTransactionEntity.Companion.REPORT_KEY
import com.taxapprf.domain.Sync

@Entity(
    tableName = TABLE_NAME,
    primaryKeys = [TRANSACTION_KEY, ACCOUNT_KEY, REPORT_KEY]
)
data class LocalTransactionEntity(
    @ColumnInfo(name = TRANSACTION_KEY)
    override val key: String,

    @ColumnInfo(name = ACCOUNT_KEY)
    val accountKey: String,
    @ColumnInfo(name = REPORT_KEY)
    val reportKey: String,

    @ColumnInfo(name = NAME)
    val name: String?,
    @ColumnInfo(name = DATE)
    val date: String,
    @ColumnInfo(name = TYPE)
    val type: String,
    @ColumnInfo(name = CURRENCY)
    val currency: String,
    @ColumnInfo(name = RATE_CBR)
    val rateCBR: Double,
    @ColumnInfo(name = SUM)
    val sum: Double,
    @ColumnInfo(name = TAX)
    val tax: Double,

    @ColumnInfo(name = IS_SYNC)
    override val isSync: Boolean,
    @ColumnInfo(name = IS_DEFERRED_DELETE)
    override val isDeferredDelete: Boolean,
    @ColumnInfo(name = SYNC_AT)
    override val syncAt: Long,
) : Sync {
    companion object {
        const val TABLE_NAME = "transaction"

        const val TRANSACTION_KEY = "transaction_key"
        const val ACCOUNT_KEY = "account_key"
        const val REPORT_KEY = "report_key"

        const val NAME = "name"
        const val DATE = "date"
        const val TYPE = "type"
        const val CURRENCY = "currency"
        const val RATE_CBR = "rate_cbr"
        const val SUM = "sum"
        const val TAX = "tax"

        const val IS_SYNC = "is_sync"
        const val IS_DEFERRED_DELETE = "is_deferred_delete"
        const val SYNC_AT = "sync_at"
    }
}