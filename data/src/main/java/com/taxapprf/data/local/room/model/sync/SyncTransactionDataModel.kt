package com.taxapprf.data.local.room.model.sync

import androidx.room.ColumnInfo
import com.taxapprf.data.local.room.LocalDatabase.Companion.CURRENCY_ORDINAL
import com.taxapprf.data.local.room.LocalDatabase.Companion.TRANSACTION_ID
import com.taxapprf.data.local.room.LocalDatabase.Companion.TYPE_ORDINAL
import com.taxapprf.data.local.room.entity.LocalCurrencyRateEntity.Companion.CURRENCY_RATE
import com.taxapprf.data.local.room.entity.LocalTransactionEntity.Companion.DATE
import com.taxapprf.data.local.room.entity.LocalTransactionEntity.Companion.NAME
import com.taxapprf.data.local.room.entity.LocalTransactionEntity.Companion.SUM
import com.taxapprf.data.local.room.entity.LocalTransactionEntity.Companion.SUM_RUB
import com.taxapprf.data.local.room.entity.LocalTransactionEntity.Companion.TAX_RUB
import com.taxapprf.data.sync.IS_SYNC
import com.taxapprf.data.sync.ISyncLocalModel
import com.taxapprf.data.sync.REMOTE_KEY
import com.taxapprf.data.sync.SYNC_AT

data class SyncTransactionDataModel(
    @ColumnInfo(name = TRANSACTION_ID)
    val transactionId: Int,

    @ColumnInfo(name = NAME)
    val name: String?,
    @ColumnInfo(name = DATE)
    val date: Long,
    @ColumnInfo(name = TYPE_ORDINAL)
    val typeOrdinal: Int,
    @ColumnInfo(name = CURRENCY_ORDINAL)
    val currencyOrdinal: Int,
    @ColumnInfo(name = CURRENCY_RATE)
    val currencyRate: Double?,
    @ColumnInfo(name = SUM)
    val sum: Double,
    @ColumnInfo(name = SUM_RUB)
    val sumRUB: Double?,
    @ColumnInfo(name = TAX_RUB)
    val taxRUB: Double?,
    @ColumnInfo(name = REMOTE_KEY)
    override val remoteKey: String?,
    @ColumnInfo(name = IS_SYNC)
    override val isSync: Boolean,
    @ColumnInfo(name = SYNC_AT)
    override val syncAt: Long,
) : ISyncLocalModel