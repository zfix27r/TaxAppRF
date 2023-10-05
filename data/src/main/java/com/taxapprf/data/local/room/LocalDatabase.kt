package com.taxapprf.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.taxapprf.data.local.room.entity.LocalAccountEntity
import com.taxapprf.data.local.room.entity.LocalCBRRateEntity
import com.taxapprf.data.local.room.entity.LocalDeletedKeyEntity
import com.taxapprf.data.local.room.entity.LocalReportEntity
import com.taxapprf.data.local.room.entity.LocalTransactionEntity
import com.taxapprf.data.local.room.entity.LocalUserEntity

@Database(
    entities = [
        LocalUserEntity::class,
        LocalAccountEntity::class,
        LocalReportEntity::class,
        LocalTransactionEntity::class,
        LocalCBRRateEntity::class,
        LocalDeletedKeyEntity::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun userDao(): LocalUserDao
    abstract fun accountDao(): LocalAccountDao
    abstract fun reportDao(): LocalReportDao
    abstract fun transactionDao(): LocalTransactionDao
    abstract fun cbrDao(): LocalCBRDao
    abstract fun deletedKeyDao(): LocalDeletedKeyDao
    abstract fun syncDao(): LocalSyncDao

    companion object {
        const val ID = "id"
        const val USER_ID = "user_id"
        const val ACCOUNT_ID = "account_id"
        const val REPORT_ID = "report_id"
        const val TRANSACTION_ID = "transaction_id"

        const val TYPE_ORDINAL = "type_ordinal"
        const val CURRENCY_ORDINAL = "currency_ordinal"

        const val DEFAULT_ID = 0
    }
}