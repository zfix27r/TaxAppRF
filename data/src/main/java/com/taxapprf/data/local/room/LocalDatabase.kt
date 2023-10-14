package com.taxapprf.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.taxapprf.data.local.room.entity.LocalAccountEntity
import com.taxapprf.data.local.room.entity.LocalCurrencyRateEntity
import com.taxapprf.data.local.room.entity.LocalDeletedEntity
import com.taxapprf.data.local.room.entity.LocalReportEntity
import com.taxapprf.data.local.room.entity.LocalTransactionEntity
import com.taxapprf.data.local.room.entity.LocalUserEntity

@Database(
    entities = [
        LocalUserEntity::class,
        LocalAccountEntity::class,
        LocalReportEntity::class,
        LocalTransactionEntity::class,
        LocalCurrencyRateEntity::class,
        LocalDeletedEntity::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun mainDao(): LocalMainDao
    abstract fun syncDao(): LocalSyncDao
    abstract fun reportsDao(): LocalReportsDao
    abstract fun transactionsDao(): LocalTransactionsDao
    abstract fun transactionDetailDao(): LocalTransactionDetailDao
    abstract fun currencyDao(): LocalCurrencyDao
    abstract fun deletedDao(): LocalDeletedDao
    abstract fun excelDao(): LocalExcelDao

    companion object {
        const val ID = "id"
        const val USER_ID = "user_id"
        const val ACCOUNT_ID = "account_id"
        const val REPORT_ID = "report_id"
        const val TRANSACTION_ID = "transaction_id"

        const val NAME = "name"
        const val TYPE_ORDINAL = "type_ordinal"
        const val CURRENCY_ORDINAL = "currency_ordinal"

        const val DEFAULT_ID = 0
    }
}