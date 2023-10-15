package com.taxapprf.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taxapprf.data.local.room.LocalDatabase.Companion.ID
import com.taxapprf.data.local.room.LocalDatabase.Companion.REPORT_ID
import com.taxapprf.data.local.room.LocalDatabase.Companion.TRANSACTION_ID
import com.taxapprf.data.local.room.entity.LocalDeletedEntity
import com.taxapprf.data.local.room.entity.LocalReportEntity.Companion.SIZE
import com.taxapprf.data.local.room.entity.LocalReportEntity.Companion.TAX_RUB
import com.taxapprf.data.local.room.model.transaction.DeletedReportDataModel
import com.taxapprf.data.local.room.model.transaction.DeletedTransactionDataModel
import com.taxapprf.data.remote.firebase.Firebase.Companion.ACCOUNT_KEY
import com.taxapprf.data.remote.firebase.Firebase.Companion.REPORT_KEY
import com.taxapprf.data.sync.REMOTE_KEY
import com.taxapprf.data.sync.SYNC_AT

@Dao
interface LocalDeletedDao {
    @Query(
        "SELECT " +
                "r.id $ID," +
                "r.tax_rub $TAX_RUB," +
                "r.size $SIZE, " +
                "a.remote_key $ACCOUNT_KEY, " +
                "r.remote_key $REPORT_KEY " +
                "FROM report r " +
                "JOIN account a ON a.id = r.account_id " +
                "WHERE r.id = :reportId LIMIT 1"
    )
    fun getGetDeletedReport(reportId: Int): DeletedReportDataModel?

    @Query(
        "SELECT " +
                "id $TRANSACTION_ID, " +
                "report_id $REPORT_ID, " +
                "tax_rub $TAX_RUB, " +
                "remote_key $REMOTE_KEY, " +
                "sync_at $SYNC_AT " +
                "FROM `transaction` " +
                "WHERE id IN (:transactionsIds)"
    )
    fun getGetDeletedTransactionsByTransactionIds(transactionsIds: List<Int>): List<DeletedTransactionDataModel>

    @Query(
        "SELECT " +
                "id $TRANSACTION_ID, " +
                "report_id $REPORT_ID, " +
                "tax_rub $TAX_RUB, " +
                "remote_key $REMOTE_KEY, " +
                "sync_at $SYNC_AT " +
                "FROM `transaction` " +
                "WHERE report_id = :reportId"
    )
    fun getGetDeletedTransactionsByReportId(reportId: Int): List<DeletedTransactionDataModel>

    @Query("UPDATE report SET tax_rub = :taxRUB, size = :size WHERE id = :reportId")
    fun updateLocalReportEntity(reportId: Int, taxRUB: Double, size: Int): Int

    @Query("DELETE FROM report WHERE id = :reportId")
    fun deleteLocalReportEntity(reportId: Int): Int

    @Query("DELETE FROM `transaction` WHERE id IN (:transactionsIds)")
    fun deleteLocalTransactionEntities(transactionsIds: List<Int>): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveLocalDeletedEntity(localDeletedKeyEntities: List<LocalDeletedEntity>): List<Long>
}