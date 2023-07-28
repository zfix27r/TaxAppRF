package com.taxapprf.data.di

import android.content.Context
import androidx.room.Room
import com.taxapprf.data.FirebaseAPI
import com.taxapprf.data.local.AppDatabase
import com.taxapprf.data.local.dao.AccountDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    private const val DB_NAME = "tax_app"

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME).build()

    @Singleton
    @Provides
    fun provideActivityDao(db: AppDatabase) = db.accountDao()

    @Singleton
    @Provides
    fun provideMainDao(db: AppDatabase) = db.mainDao()

    @Singleton
    @Provides
    fun provideFirebase(dao: AccountDao) = FirebaseAPI(dao)
}