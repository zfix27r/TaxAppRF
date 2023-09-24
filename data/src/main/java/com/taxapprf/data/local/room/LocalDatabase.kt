package com.taxapprf.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.taxapprf.data.local.room.dao.LocalAccountDao
import com.taxapprf.data.local.room.dao.LocalReportDao
import com.taxapprf.data.local.room.dao.LocalTransactionDao
import com.taxapprf.data.local.room.dao.LocalUserDao
import com.taxapprf.data.local.room.entity.LocalAccountEntity
import com.taxapprf.data.local.room.entity.LocalReportEntity
import com.taxapprf.data.local.room.entity.LocalTransactionEntity
import com.taxapprf.data.local.room.entity.LocalUserEntity

@Database(
    entities = [
        LocalUserEntity::class,
        LocalAccountEntity::class,
        LocalReportEntity::class,
        LocalTransactionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun userDao(): LocalUserDao
    abstract fun accountDao(): LocalAccountDao
    abstract fun reportDao(): LocalReportDao
    abstract fun transactionDao(): LocalTransactionDao
}