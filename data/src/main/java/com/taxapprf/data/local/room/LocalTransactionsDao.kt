package com.taxapprf.data.local.room

import androidx.room.Dao
import androidx.room.Query
import com.taxapprf.data.local.room.LocalDatabase.Companion.CURRENCY_ORDINAL
import com.taxapprf.data.local.room.LocalDatabase.Companion.ID
import com.taxapprf.data.local.room.LocalDatabase.Companion.NAME
import com.taxapprf.data.local.room.LocalDatabase.Companion.TYPE_ORDINAL
import com.taxapprf.data.local.room.entity.LocalCurrencyRateEntity.Companion.CURRENCY_RATE
import com.taxapprf.data.local.room.entity.LocalReportEntity
import com.taxapprf.data.local.room.entity.LocalTransactionEntity.Companion.DATE
import com.taxapprf.data.local.room.entity.LocalTransactionEntity.Companion.SUM
import com.taxapprf.data.local.room.entity.LocalTransactionEntity.Companion.TAX_RUB
import com.taxapprf.data.local.room.model.ObserveTransactionDataModel
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalTransactionsDao {
    @Query("SELECT * FROM report WHERE id = :reportId LIMIT 1")
    fun observeLocalReportEntity(reportId: Int): Flow<LocalReportEntity?>

    @Query(
        "SELECT " +
                "t.id $ID, " +
                "t.name $NAME, " +
                "t.date $DATE, " +
                "t.type_ordinal $TYPE_ORDINAL, " +
                "t.sum $SUM, " +
                "t.tax_rub $TAX_RUB, " +
                "t.currency_ordinal $CURRENCY_ORDINAL, " +
                "r.currency_rate $CURRENCY_RATE " +
                "FROM `transaction` t " +
                "LEFT JOIN cbr_rate r ON r.currency_ordinal = t.currency_ordinal AND r.date = t.date " +
                "WHERE t.report_id = :reportId " +
                "ORDER BY t.date ASC"
    )
    fun observeListGetTransaction(reportId: Int): Flow<List<ObserveTransactionDataModel>>
}