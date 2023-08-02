package com.taxapprf.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.taxapprf.data.local.dao.AccountDao
import com.taxapprf.data.local.dao.TaxDao
import com.taxapprf.data.local.dao.UserDao
import com.taxapprf.data.local.entity.AccountEntity
import com.taxapprf.data.local.entity.TaxEntity
import com.taxapprf.data.local.entity.TransactionEntity
import com.taxapprf.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        AccountEntity::class,
        TaxEntity::class,
        TransactionEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun accountDao(): AccountDao
    abstract fun taxDao(): TaxDao
}