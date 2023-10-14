package com.taxapprf.data.local.room

import androidx.room.Dao
import androidx.room.Query
import com.taxapprf.data.local.room.entity.LocalCurrencyRateEntity
import com.taxapprf.data.local.room.entity.LocalReportEntity
import com.taxapprf.data.local.room.entity.LocalTransactionEntity
import com.taxapprf.data.local.room.model.GetExcelTransaction

@Dao
interface LocalExcelDao {
    @Query("SELECT * FROM report WHERE id = :reportId LIMIT 1")
    fun getLocalReportEntity(reportId: Int): LocalReportEntity?

    @Query(
        "SELECT " +
                "t.name ${LocalTransactionEntity.NAME}, " +
                "t.date ${LocalTransactionEntity.DATE}, " +
                "t.type_ordinal ${LocalDatabase.TYPE_ORDINAL}, " +
                "t.currency_ordinal ${LocalDatabase.CURRENCY_ORDINAL}, " +
                "t.sum ${LocalTransactionEntity.SUM}, " +
                "t.tax ${LocalTransactionEntity.TAX}, " +
                "r.currency_rate ${LocalCurrencyRateEntity.CURRENCY_RATE} " +
                "FROM `transaction` t " +
                "LEFT JOIN cbr_rate r ON r.currency_ordinal = t.currency_ordinal AND r.date = t.date " +
                "WHERE report_id = :reportId"
    )
    fun getExcelTransactions(reportId: Int): List<GetExcelTransaction>
}