package com.taxapprf.data.local.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taxapprf.data.local.room.LocalDatabase.Companion.CURRENCY_ORDINAL
import com.taxapprf.data.local.room.LocalDatabase.Companion.TRANSACTION_ID
import com.taxapprf.data.local.room.LocalDatabase.Companion.TYPE_ORDINAL
import com.taxapprf.data.local.room.entity.LocalCBRRateEntity.Companion.CURRENCY_RATE
import com.taxapprf.data.local.room.entity.LocalTransactionEntity
import com.taxapprf.data.local.room.entity.LocalTransactionEntity.Companion.DATE
import com.taxapprf.data.local.room.entity.LocalTransactionEntity.Companion.NAME
import com.taxapprf.data.local.room.entity.LocalTransactionEntity.Companion.SUM
import com.taxapprf.data.local.room.entity.LocalTransactionEntity.Companion.TAX
import com.taxapprf.data.local.room.model.sync.GetSyncTransactionModel
import com.taxapprf.data.remote.firebase.FirebaseAPI.Companion.TRANSACTION_KEY
import com.taxapprf.data.sync.SYNC_AT

@Dao
interface LocalSyncDao {
    @Query(
        "SELECT " +
                "t.id ${TRANSACTION_ID}, " +
                "t.name $NAME, " +
                "t.date $DATE, " +
                "t.type_ordinal $TYPE_ORDINAL, " +
                "t.currency_ordinal $CURRENCY_ORDINAL, " +
                "r.currency_rate $CURRENCY_RATE, " +
                "t.sum $SUM, " +
                "t.tax $TAX, " +
                "t.remote_key $TRANSACTION_KEY, " +
                "t.sync_at $SYNC_AT " +
                "FROM `transaction` t " +
                "LEFT JOIN cbr_rate r ON r.currency_ordinal = t.currency_ordinal AND r.date = t.date " +
                "WHERE t.report_id = :reportId"
    )
    fun getTransactions(reportId: Int): List<GetSyncTransactionModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveTransactions(localTransactionEntities: List<LocalTransactionEntity>): List<Long>

    @Delete
    fun deleteTransactions(localTransactionEntities: List<LocalTransactionEntity>): Int
}