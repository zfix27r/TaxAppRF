package com.taxapprf.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.taxapprf.data.local.dao.AccountDao
import com.taxapprf.data.local.dao.MainDao
import com.taxapprf.data.local.dao.TransactionDao
import com.taxapprf.data.local.dao.UserDao
import com.taxapprf.data.local.entity.AccountEntity
import com.taxapprf.data.local.entity.TaxesEntity
import com.taxapprf.data.local.entity.TransactionEntity
import com.taxapprf.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        AccountEntity::class,
        TaxesEntity::class,
        TransactionEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    // TODO слить в один дао user, account. Дропнуть мейн.
    abstract fun accountDao(): AccountDao
    abstract fun mainDao(): MainDao
    abstract fun transactionDao(): TransactionDao
}