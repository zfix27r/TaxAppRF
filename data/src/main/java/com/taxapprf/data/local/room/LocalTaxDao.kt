package com.taxapprf.data.local.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
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
import com.taxapprf.data.local.room.model.tax.TransactionSumRUBDataModel
import com.taxapprf.data.local.room.model.tax.TransactionTaxRUBDataModel

@Dao
interface LocalTaxDao {
    /* UPDATE SUM */
    @Query(
        "SELECT " +
                "id $ID, " +
                "report_id $REPORT_ID, " +
                "currency_ordinal $CURRENCY_ORDINAL, " +
                "date $DATE, " +
                "sum $SUM, " +
                "sum_rub $SUM_RUB, " +
                "tax_rub $TAX_RUB " +
                "FROM `transaction` " +
                "WHERE sum_rub IS NULL"
    )
    fun getTransactionSumRUBModels(): List<TransactionSumRUBDataModel>

    @Query("SELECT sum_rub FROM report WHERE id = :reportId LIMIT 1")
    fun getReportSumRUB(reportId: Int): Double?

    @Update(entity = LocalTransactionEntity::class)
    fun updateTransactionTaxModels(transactionSumRUBModels: List<TransactionSumRUBDataModel>)

    @Query("UPDATE report SET sum_rub = (SELECT SUM(sum_rub) FROM `transaction` WHERE report_id = :reportId) WHERE id = :reportId")
    fun updateReportSumRUB(reportId: Int)

    @Transaction
    fun updateAllUpdatedSumRUB(
        reportIds: Set<Int>,
        transactionSumRUBModels: List<TransactionSumRUBDataModel>
    ) {
        updateTransactionTaxModels(transactionSumRUBModels)

        reportIds.forEach { reportId ->
            updateReportSumRUB(reportId)
        }
    }


    /* UPDATE TAX */
    @Query(
        "SELECT " +
                "id $ID, " +
                "report_id $REPORT_ID, " +
                "type_ordinal $TYPE_ORDINAL, " +
                "sum_rub $SUM_RUB, " +
                "tax_rub $TAX_RUB " +
                "FROM `transaction` " +
                "WHERE tax_rub IS NULL"
    )
    fun getTransactionTaxRUBModels(): List<TransactionTaxRUBDataModel>

    @Update(entity = LocalTransactionEntity::class)
    fun updateTransactionTaxRUBModels(transactionTaxRUBModels: List<TransactionTaxRUBDataModel>)

    @Query("UPDATE report SET tax_rub = (SELECT SUM(tax_rub) FROM `transaction` WHERE report_id = :reportId) WHERE id = :reportId")
    fun updateReportTaxRUB(reportId: Int)

    @Transaction
    fun updateAllUpdatedTaxRUB(
        reportMap: Map<Int, Double>,
        transactionTaxRUBModels: List<TransactionTaxRUBDataModel>
    ) {
        updateTransactionTaxRUBModels(transactionTaxRUBModels)

        reportMap.forEach { report ->
            updateReportTaxRUB(report.key)
        }
    }
}