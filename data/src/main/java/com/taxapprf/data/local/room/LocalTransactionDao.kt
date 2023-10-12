package com.taxapprf.data.local.room

import androidx.room.Dao
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
import com.taxapprf.data.local.room.model.GetExcelTransaction
import com.taxapprf.data.local.room.model.GetTransaction
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalTransactionDao {
    @Query(
        "SELECT " +
                "t.id $TRANSACTION_ID, " +
                "t.name $NAME, " +
                "t.date $DATE, " +
                "t.type_ordinal $TYPE_ORDINAL, " +
                "t.sum $SUM, " +
                "t.tax $TAX, " +
                "t.currency_ordinal $CURRENCY_ORDINAL, " +
                "r.currency_rate $CURRENCY_RATE " +
                "FROM `transaction` t " +
                "LEFT JOIN cbr_rate r ON r.currency_ordinal = t.currency_ordinal AND r.date = t.date " +
                "WHERE report_id = :reportId"
    )
    fun observeReport(reportId: Int): Flow<List<GetTransaction>>

    @Query(
        "SELECT " +
                "t.id $TRANSACTION_ID, " +
                "t.name $NAME, " +
                "t.date $DATE, " +
                "t.type_ordinal $TYPE_ORDINAL, " +
                "t.sum $SUM, " +
                "t.tax $TAX, " +
                "t.currency_ordinal $CURRENCY_ORDINAL, " +
                "r.currency_rate $CURRENCY_RATE " +
                "FROM `transaction` t " +
                "LEFT JOIN cbr_rate r ON r.currency_ordinal = t.currency_ordinal AND r.date = t.date " +
                "WHERE t.id = :transactionId LIMIT 1"
    )
    fun observe(transactionId: Int): Flow<GetTransaction?>

    @Query(
        "SELECT " +
                "t.name $NAME, " +
                "t.date $DATE, " +
                "t.type_ordinal $TYPE_ORDINAL, " +
                "t.currency_ordinal $CURRENCY_ORDINAL, " +
                "t.sum $SUM, " +
                "t.tax $TAX, " +
                "r.currency_rate $CURRENCY_RATE " +
                "FROM `transaction` t " +
                "LEFT JOIN cbr_rate r ON r.currency_ordinal = t.currency_ordinal AND r.date = t.date " +
                "WHERE report_id = :reportId"
    )
    fun getExcelTransactionsWithCurrency(reportId: Int): List<GetExcelTransaction>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(localTransactionEntity: LocalTransactionEntity): Long

    @Query("DELETE FROM `transaction` WHERE id = :transactionId")
    fun delete(transactionId: Int): Int

    @Query("UPDATE `transaction` SET tax = :tax WHERE id = :transactionId")
    fun updateTax(transactionId: Int, tax: Double)

    @Query("DELETE FROM `transaction`")
    fun deleteAll()
}