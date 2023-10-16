package com.taxapprf.data.local.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import com.taxapprf.data.local.room.LocalDatabase.Companion.CURRENCY_ORDINAL
import com.taxapprf.data.local.room.LocalDatabase.Companion.ID
import com.taxapprf.data.local.room.LocalDatabase.Companion.REPORT_ID
import com.taxapprf.data.local.room.LocalDatabase.Companion.TYPE_ORDINAL
import com.taxapprf.data.local.room.entity.LocalTransactionEntity
import com.taxapprf.data.local.room.entity.LocalTransactionEntity.Companion.DATE
import com.taxapprf.data.local.room.entity.LocalTransactionEntity.Companion.SUM
import com.taxapprf.data.local.room.entity.LocalTransactionEntity.Companion.SUM_RUB
import com.taxapprf.data.local.room.entity.LocalTransactionEntity.Companion.TAX_RUB
import com.taxapprf.data.local.room.model.tax.TransactionSumRUBAndTaxRUBDataModel

@Dao
interface LocalTaxDao {
    /* UPDATE SUM */
    @Query(
        "SELECT " +
                "id $ID, " +
                "report_id $REPORT_ID, " +
                "type_ordinal $TYPE_ORDINAL, " +
                "currency_ordinal $CURRENCY_ORDINAL, " +
                "date $DATE, " +
                "sum $SUM, " +
                "sum_rub $SUM_RUB, " +
                "tax_rub $TAX_RUB " +
                "FROM `transaction` " +
                "WHERE sum_rub IS NULL"
    )
    fun getTransactionSumRUBAndTaxRUBDataModels(): List<TransactionSumRUBAndTaxRUBDataModel>

    @Update(entity = LocalTransactionEntity::class)
    fun updateTransactionSumRUBAndTaxRUBDataModels(transactionSumRUBModels: List<TransactionSumRUBAndTaxRUBDataModel>)

    @Query("SELECT sum_rub FROM report WHERE id = :reportId LIMIT 1")
    fun getReportSumRUB(reportId: Int): Double?

    @Query("UPDATE report SET sum_rub = (SELECT SUM(sum_rub) FROM `transaction` WHERE report_id = :reportId) WHERE id = :reportId")
    fun updateReportSumRUB(reportId: Int)

    @Query(
        "SELECT " +
                "id $ID, " +
                "report_id $REPORT_ID, " +
                "type_ordinal $TYPE_ORDINAL, " +
                "currency_ordinal $CURRENCY_ORDINAL, " +
                "date $DATE, " +
                "sum $SUM, " +
                "sum_rub $SUM_RUB, " +
                "tax_rub $TAX_RUB " +
                "FROM `transaction` " +
                "WHERE report_id =:reportId"
    )
    fun getTransactionSumRUBAndTaxRUBDataModels(reportId: Int): List<TransactionSumRUBAndTaxRUBDataModel>

    @Query("UPDATE report SET tax_rub = (SELECT SUM(tax_rub) FROM `transaction` WHERE report_id = :reportId) WHERE id = :reportId")
    fun updateReportTaxRUB(reportId: Int)
}