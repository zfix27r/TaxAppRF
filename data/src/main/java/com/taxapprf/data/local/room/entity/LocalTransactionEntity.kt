package com.taxapprf.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.taxapprf.data.getEpochTime
import com.taxapprf.data.local.room.LocalDatabase.Companion.CURRENCY_ORDINAL
import com.taxapprf.data.local.room.LocalDatabase.Companion.DEFAULT_ID
import com.taxapprf.data.local.room.LocalDatabase.Companion.ID
import com.taxapprf.data.local.room.LocalDatabase.Companion.REPORT_ID
import com.taxapprf.data.local.room.LocalDatabase.Companion.TYPE_ORDINAL
import com.taxapprf.data.local.room.entity.LocalTransactionEntity.Companion.TABLE_NAME
import com.taxapprf.data.sync.IS_SYNC
import com.taxapprf.data.sync.ISyncLocalModel
import com.taxapprf.data.sync.REMOTE_KEY
import com.taxapprf.data.sync.SYNC_AT

@Entity(tableName = TABLE_NAME)
data class LocalTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    val id: Int = DEFAULT_ID,

    @ColumnInfo(name = REPORT_ID)
    val reportId: Int,

    @ColumnInfo(name = TYPE_ORDINAL)
    val typeOrdinal: Int,
    @ColumnInfo(name = CURRENCY_ORDINAL)
    val currencyOrdinal: Int,

    @ColumnInfo(name = NAME)
    val name: String? = null,
    @ColumnInfo(name = DATE)
    val date: Long,
    @ColumnInfo(name = SUM)
    val sum: Double,
    @ColumnInfo(name = SUM_RUB)
    val sumRUB: Double? = null,
    @ColumnInfo(name = TAX_RUB)
    val taxRUB: Double? = null,

    @ColumnInfo(name = REMOTE_KEY)
    override val remoteKey: String? = null,
    @ColumnInfo(name = IS_SYNC)
    override val isSync: Boolean = false,
    @ColumnInfo(name = SYNC_AT)
    override val syncAt: Long = getEpochTime(),
) : ISyncLocalModel {
    companion object {
        const val TABLE_NAME = "transaction"

        const val NAME = "name"
        const val DATE = "date"
        const val SUM = "sum"
        const val SUM_RUB = "sum_rub"
        const val TAX_RUB = "tax_rub"
    }
}