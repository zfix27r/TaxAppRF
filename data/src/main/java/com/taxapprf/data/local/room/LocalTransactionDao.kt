package com.taxapprf.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taxapprf.data.local.room.LocalDatabase.Companion.CURRENCY_ORDINAL
import com.taxapprf.data.local.room.LocalDatabase.Companion.REPORT_ID
import com.taxapprf.data.local.room.LocalDatabase.Companion.TRANSACTION_ID
import com.taxapprf.data.local.room.LocalDatabase.Companion.TYPE_ORDINAL
import com.taxapprf.data.local.room.entity.LocalCBRRateEntity.Companion.CURRENCY_RATE
import com.taxapprf.data.local.room.entity.LocalTransactionEntity
import com.taxapprf.data.local.room.entity.LocalTransactionEntity.Companion.DATE
import com.taxapprf.data.local.room.entity.LocalTransactionEntity.Companion.NAME
import com.taxapprf.data.local.room.entity.LocalTransactionEntity.Companion.SUM
import com.taxapprf.data.local.room.entity.LocalTransactionEntity.Companion.TAX
import com.taxapprf.data.local.room.model.GetExcelTransaction
import com.taxapprf.data.local.room.model.GetKeysTransaction
import com.taxapprf.data.local.room.model.GetTransaction
import com.taxapprf.data.remote.firebase.FirebaseAPI.Companion.ACCOUNT_KEY
import com.taxapprf.data.remote.firebase.FirebaseAPI.Companion.REPORT_KEY
import com.taxapprf.data.remote.firebase.FirebaseAPI.Companion.TRANSACTION_KEY
import com.taxapprf.data.sync.SYNC_AT
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

    @Query(
        "SELECT " +
                "t.id $TRANSACTION_ID, " +
                "r.id $REPORT_ID, " +
                "t.tax $TAX, " +
                "a.remote_key $ACCOUNT_KEY, " +
                "r.remote_key $REPORT_KEY, " +
                "t.remote_key $TRANSACTION_KEY, " +
                "t.sync_at $SYNC_AT " +
                "FROM `transaction` t " +
                "LEFT JOIN report r ON r.id = t.report_id " +
                "LEFT JOIN account a ON a.id = r.account_id " +
                "WHERE t.id = :transactionId LIMIT 1"
    )
    fun getKeysTransaction(transactionId: Int): GetKeysTransaction?

    @Query(
        "SELECT " +
                "t.id $TRANSACTION_ID, " +
                "r.id $REPORT_ID, " +
                "t.tax $TAX, " +
                "a.remote_key $ACCOUNT_KEY, " +
                "r.remote_key $REPORT_KEY, " +
                "t.remote_key $TRANSACTION_KEY, " +
                "t.sync_at $SYNC_AT " +
                "FROM `transaction` t " +
                "LEFT JOIN report r ON r.id = t.report_id " +
                "LEFT JOIN account a ON a.id = r.account_id " +
                "WHERE t.report_id = :reportId"
    )
    fun getKeysTransactions(reportId: Int): List<GetKeysTransaction>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(localTransactionEntity: LocalTransactionEntity): Long

    @Query("DELETE FROM `transaction` WHERE id = :transactionId")
    fun delete(transactionId: Int): Int

    @Query("UPDATE `transaction` SET tax = :tax WHERE id = :transactionId")
    fun updateTax(transactionId: Int, tax: Double)

    @Query("DELETE FROM `transaction`")
    fun deleteAll()
}