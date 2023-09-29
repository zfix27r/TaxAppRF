package com.taxapprf.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taxapprf.data.REPORT_ID
import com.taxapprf.data.TRANSACTION_ID
import com.taxapprf.data.local.room.entity.LocalTransactionEntity
import com.taxapprf.data.local.room.model.GetExcelTransactionWithCurrency
import com.taxapprf.data.local.room.model.GetTransactionKeys
import com.taxapprf.data.local.room.model.GetTransactionWithCurrency
import com.taxapprf.data.sync.ACCOUNT_KEY
import com.taxapprf.data.sync.REPORT_KEY
import com.taxapprf.data.sync.SYNC_AT
import com.taxapprf.data.sync.TRANSACTION_KEY
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalTransactionDao {
    @Query(
        "SELECT " +
                "t.id ${LocalTransactionEntity.ID}, " +
                "t.name ${LocalTransactionEntity.NAME}, " +
                "t.date ${LocalTransactionEntity.DATE}, " +
                "t.type ${LocalTransactionEntity.TYPE}, " +
                "t.sum ${LocalTransactionEntity.SUM}, " +
                "t.tax ${LocalTransactionEntity.TAX}, " +
                "c.id ${GetTransactionWithCurrency.CURRENCY_ID}, " +
                "c.char_code ${GetTransactionWithCurrency.CURRENCY_CHAR_CODE}, " +
                "r.rate ${GetTransactionWithCurrency.CURRENCY_RATE} " +
                "FROM `transaction` t " +
                "LEFT JOIN cbr_currency c ON c.id = t.currency_id " +
                "LEFT JOIN cbr_rate r ON r.currency_id = t.currency_id AND r.date = t.date " +
                "WHERE report_id = :reportId"
    )
    fun observeReport(reportId: Int): Flow<List<GetTransactionWithCurrency>>

    @Query(
        "SELECT " +
                "t.id ${LocalTransactionEntity.ID}, " +
                "t.name ${LocalTransactionEntity.NAME}, " +
                "t.date ${LocalTransactionEntity.DATE}, " +
                "t.type ${LocalTransactionEntity.TYPE}, " +
                "t.sum ${LocalTransactionEntity.SUM}, " +
                "t.tax ${LocalTransactionEntity.TAX}, " +
                "c.id ${GetTransactionWithCurrency.CURRENCY_ID}, " +
                "c.char_code ${GetTransactionWithCurrency.CURRENCY_CHAR_CODE}, " +
                "r.rate ${GetTransactionWithCurrency.CURRENCY_RATE} " +
                "FROM `transaction` t " +
                "LEFT JOIN cbr_currency c ON c.id = t.currency_id " +
                "LEFT JOIN cbr_rate r ON r.currency_id = t.currency_id AND r.date = t.date " +
                "WHERE t.id = :transactionId LIMIT 1"
    )
    fun observe(transactionId: Int): Flow<GetTransactionWithCurrency?>

    @Query(
        "SELECT " +
                "t.name ${LocalTransactionEntity.NAME}, " +
                "t.date ${LocalTransactionEntity.DATE}, " +
                "t.type ${LocalTransactionEntity.TYPE}, " +
                "t.sum ${LocalTransactionEntity.SUM}, " +
                "t.tax ${LocalTransactionEntity.TAX}, " +
                "c.char_code ${GetTransactionWithCurrency.CURRENCY_CHAR_CODE}, " +
                "r.rate ${GetTransactionWithCurrency.CURRENCY_RATE} " +
                "FROM `transaction` t " +
                "LEFT JOIN cbr_currency c ON c.id = t.currency_id " +
                "LEFT JOIN cbr_rate r ON r.currency_id = t.currency_id AND r.date = t.date " +
                "WHERE report_id = :reportId"
    )
    fun getExcelTransactionsWithCurrency(reportId: Int): List<GetExcelTransactionWithCurrency>

    @Query(
        "SELECT " +
                "t.id ${TRANSACTION_ID}, " +
                "r.id ${REPORT_ID}, " +
                "t.tax ${LocalTransactionEntity.TAX}, " +
                "a.remote_key ${ACCOUNT_KEY}, " +
                "r.remote_key ${REPORT_KEY}, " +
                "t.remote_key ${TRANSACTION_KEY}, " +
                "t.sync_at $SYNC_AT " +
                "FROM `transaction` t " +
                "LEFT JOIN account a ON a.id = t.account_id " +
                "LEFT JOIN report r ON r.id = t.report_id " +
                "WHERE t.id = :transactionId LIMIT 1"
    )
    fun getTransactionKeys(transactionId: Int): GetTransactionKeys?

    @Query(
        "SELECT " +
                "t.id ${TRANSACTION_ID}, " +
                "t.tax ${LocalTransactionEntity.TAX}, " +
                "a.remote_key ${ACCOUNT_KEY}, " +
                "r.remote_key ${REPORT_KEY}, " +
                "t.remote_key ${TRANSACTION_KEY}, " +
                "t.sync_at $SYNC_AT " +
                "FROM `transaction` t " +
                "LEFT JOIN account a ON a.id = t.account_id " +
                "LEFT JOIN report r ON r.id = t.report_id " +
                "WHERE t.report_id = :reportId"
    )
    fun getTransactionsKeys(reportId: Int): List<GetTransactionKeys>

    @Query("SELECT * FROM `transaction` WHERE account_id = :accountId AND report_id = :reportId")
    fun getAll(accountId: Int, reportId: Int): List<LocalTransactionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(localTransactionEntity: LocalTransactionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAll(transactionEntities: List<LocalTransactionEntity>): List<Long>

    @Query("DELETE FROM `transaction` WHERE account_id = :accountId AND report_id = :reportId")
    fun deleteAll(accountId: Int, reportId: Int): Int

    @Query("DELETE FROM `transaction` WHERE id = :transactionId")
    fun delete(transactionId: Int): Int

    @Query("UPDATE `transaction` SET tax = :tax WHERE id = :transactionId")
    fun updateTax(transactionId: Int, tax: Double)
}