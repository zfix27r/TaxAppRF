package com.taxapprf.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.taxapprf.data.local.room.entity.LocalAccountEntity
import com.taxapprf.data.local.room.entity.LocalCBRCurrencyEntity
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
        LocalCBRCurrencyEntity::class,
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
}