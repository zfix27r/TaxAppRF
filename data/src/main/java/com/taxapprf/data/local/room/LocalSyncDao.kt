package com.taxapprf.data.local.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.taxapprf.data.local.room.LocalDatabase.Companion.CURRENCY_ORDINAL
import com.taxapprf.data.local.room.LocalDatabase.Companion.ID
import com.taxapprf.data.local.room.LocalDatabase.Companion.TRANSACTION_ID
import com.taxapprf.data.local.room.LocalDatabase.Companion.TYPE_ORDINAL
import com.taxapprf.data.local.room.entity.LocalAccountEntity
import com.taxapprf.data.local.room.entity.LocalCBRRateEntity.Companion.CURRENCY_RATE
import com.taxapprf.data.local.room.entity.LocalDeletedEntity
import com.taxapprf.data.local.room.entity.LocalReportEntity
import com.taxapprf.data.local.room.entity.LocalReportEntity.Companion.SIZE
import com.taxapprf.data.local.room.entity.LocalTransactionEntity
import com.taxapprf.data.local.room.entity.LocalTransactionEntity.Companion.DATE
import com.taxapprf.data.local.room.entity.LocalTransactionEntity.Companion.NAME
import com.taxapprf.data.local.room.entity.LocalTransactionEntity.Companion.SUM
import com.taxapprf.data.local.room.entity.LocalTransactionEntity.Companion.TAX
import com.taxapprf.data.local.room.entity.LocalUserEntity
import com.taxapprf.data.local.room.model.sync.CountAndSumTransactionsModel
import com.taxapprf.data.local.room.model.sync.GetSyncResultAccountModel
import com.taxapprf.data.local.room.model.sync.GetSyncResultReportModel
import com.taxapprf.data.local.room.model.sync.GetSyncTransactionModel
import com.taxapprf.data.remote.firebase.Firebase.Companion.ACCOUNT_KEY
import com.taxapprf.data.remote.firebase.Firebase.Companion.REPORT_KEY
import com.taxapprf.data.sync.IS_SYNC
import com.taxapprf.data.sync.REMOTE_KEY
import com.taxapprf.data.sync.SYNC_AT

@Dao
interface LocalSyncDao {
    /* SYNC ALL */
    @Query("SELECT * FROM user WHERE email = :email LIMIT 1")
    fun getUserByEmail(email: String): LocalUserEntity?

    /* SYNC DELETED */
    @Query("SELECT * FROM deleted ORDER BY account_key, report_key")
    fun getAllDeleted(): List<LocalDeletedEntity>

    @Query("DELETE FROM deleted")
    fun deleteAllDeleted()

    /* ACCOUNT SYNC */
    @Query("SELECT * FROM account WHERE user_id = :userId")
    fun getUserAccounts(userId: Int): List<LocalAccountEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveUserAccounts(localAccountEntities: List<LocalAccountEntity>): List<Long>

    @Delete
    fun deleteUserAccounts(localAccountEntities: List<LocalAccountEntity>): Int

    @Query(
        "SELECT " +
                "id $ID, " +
                "remote_key $ACCOUNT_KEY " +
                "FROM account WHERE user_id = :userId"
    )
    fun getSyncResultUserAccounts(userId: Int): List<GetSyncResultAccountModel>

    /* REPORT SYNC */

    @Query("SELECT * FROM report WHERE account_id = :accountId")
    fun getAccountReports(accountId: Int): List<LocalReportEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAccountReports(reportEntities: List<LocalReportEntity>): List<Long>

    @Delete
    fun deleteAccountReports(reportEntities: List<LocalReportEntity>): Int

    @Query(
        "SELECT " +
                "r.id $ID, " +
                "a.remote_key $ACCOUNT_KEY, " +
                "r.remote_key $REPORT_KEY " +
                "FROM report r " +
                "LEFT JOIN account a ON a.id = r.account_id " +
                "WHERE r.account_id = :accountId"
    )
    fun getSyncResultAccountReports(accountId: Int): List<GetSyncResultReportModel>

    /* TRANSACTION SYNC */
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
                "t.remote_key $REMOTE_KEY, " +
                "t.is_sync $IS_SYNC, " +
                "t.sync_at $SYNC_AT " +
                "FROM `transaction` t " +
                "LEFT JOIN cbr_rate r ON r.currency_ordinal = t.currency_ordinal AND r.date = t.date " +
                "WHERE t.report_id = :reportId"
    )
    fun getTransactions(reportId: Int): List<GetSyncTransactionModel>

    @Query("SELECT * FROM report WHERE id = :reportId LIMIT 1")
    fun getReport(reportId: Int): LocalReportEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveReport(localReportEntity: LocalReportEntity): Long

    @Delete
    fun deleteReport(localReportEntity: LocalReportEntity): Int

    @Transaction
    fun saveTransactionsWithUpdateReport(
        reportId: Int,
        localTransactionEntities: List<LocalTransactionEntity>
    ): List<Long> {
        val result = saveTransactions(localTransactionEntities)

        return getReport(reportId)?.let { localReportEntity ->
            countAndSumTransactions(reportId)?.let {
                saveReport(localReportEntity.copy(tax = it.tax, size = it.size))
                result
            } ?: emptyList()
        } ?: emptyList()
    }

    @Transaction
    fun deleteTransactionsWithUpdateReport(
        reportId: Int,
        localTransactionEntities: List<LocalTransactionEntity>
    ) =
        getReport(reportId)?.let { localReportEntity ->
            val newSize = localReportEntity.size - localTransactionEntities.size
            var newTax = localReportEntity.tax
            localTransactionEntities.forEach { localTransactionEntity ->
                localTransactionEntity.tax?.let { newTax -= it }
            }

            if (newSize < 1) deleteReport(localReportEntity)
            else saveReport(localReportEntity.copy(tax = newTax, size = newSize))

            deleteTransactions(localTransactionEntities)
        } ?: 0

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveTransactions(localTransactionEntities: List<LocalTransactionEntity>): List<Long>

    @Delete
    fun deleteTransactions(localTransactionEntities: List<LocalTransactionEntity>): Int

    @Query("SELECT COUNT(*) $SIZE, SUM(tax) $TAX FROM `transaction` WHERE report_id = :reportId")
    fun countAndSumTransactions(reportId: Int): CountAndSumTransactionsModel?
}