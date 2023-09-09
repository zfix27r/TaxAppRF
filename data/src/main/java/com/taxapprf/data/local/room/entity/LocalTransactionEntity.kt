package com.taxapprf.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.taxapprf.data.local.room.entity.LocalTransactionEntity.Companion.TABLE_NAME
import com.taxapprf.data.sync.IS_DELETE
import com.taxapprf.data.sync.IS_SYNC
import com.taxapprf.data.sync.SYNC_AT
import com.taxapprf.data.sync.SyncLocal

@Entity(tableName = TABLE_NAME)
data class LocalTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    val id: Int,

    @ColumnInfo(name = ACCOUNT_KEY)
    val accountKey: String,
    @ColumnInfo(name = REPORT_KEY)
    val reportKey: String,
    @ColumnInfo(name = TRANSACTION_KEY)
    override val key: String,

    @ColumnInfo(name = NAME)
    val name: String?,
    @ColumnInfo(name = DATE)
    val date: String,
    @ColumnInfo(name = TYPE)
    val type: String,
    @ColumnInfo(name = CURRENCY)
    val currency: String,
    @ColumnInfo(name = RATE_CBRF)
    val rateCBRF: Double,
    @ColumnInfo(name = SUM)
    val sum: Double,
    @ColumnInfo(name = TAX)
    val tax: Double,

    @ColumnInfo(name = IS_SYNC)
    override val isSync: Boolean,
    @ColumnInfo(name = IS_DELETE)
    override val isDelete: Boolean,
    @ColumnInfo(name = SYNC_AT)
    override val syncAt: Long,
) : SyncLocal {
    companion object {
        const val TABLE_NAME = "transaction"

        const val ID = "id"

        const val TRANSACTION_KEY = "transaction_key"
        const val ACCOUNT_KEY = "account_key"
        const val REPORT_KEY = "report_key"

        const val NAME = "name"
        const val DATE = "date"
        const val TYPE = "type"
        const val CURRENCY = "currency"
        const val RATE_CBRF = "rate_cbrf"
        const val SUM = "sum"
        const val TAX = "tax"
    }
}