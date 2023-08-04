package com.taxapprf.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.taxapprf.data.local.room.dao.AccountDao
import com.taxapprf.data.local.room.dao.TaxDao
import com.taxapprf.data.local.room.dao.TransactionDao
import com.taxapprf.data.local.room.dao.UserDao
import com.taxapprf.data.local.room.entity.AccountEntity
import com.taxapprf.data.local.room.entity.TaxEntity
import com.taxapprf.data.local.room.entity.TransactionEntity
import com.taxapprf.data.local.room.entity.UserEntity

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
    abstract fun transactionDao(): TransactionDao
}