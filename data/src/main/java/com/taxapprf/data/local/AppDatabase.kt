package com.taxapprf.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.taxapprf.data.local.dao.ActivityDao
import com.taxapprf.data.local.dao.MainDao
import com.taxapprf.data.local.entity.UserEntity

@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun activityDao(): ActivityDao
    abstract fun mainDao(): MainDao
}