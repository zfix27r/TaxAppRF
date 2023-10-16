package com.taxapprf.data.di

import android.content.Context
import android.net.ConnectivityManager
import androidx.room.Room
import com.taxapprf.data.NetworkManager
import com.taxapprf.data.local.room.LocalDatabase
import com.taxapprf.data.local.room.LocalSyncDao
import com.taxapprf.data.remote.cbr.RemoteCurrencyDao
import com.taxapprf.data.remote.firebase.Firebase
import com.taxapprf.data.remote.firebase.FirebaseAccountDaoImpl
import com.taxapprf.data.remote.firebase.FirebaseReportDaoImpl
import com.taxapprf.data.remote.firebase.FirebaseTransactionDaoImpl
import com.taxapprf.data.remote.firebase.FirebaseUserDaoImpl
import com.taxapprf.data.remote.firebase.dao.RemoteAccountDao
import com.taxapprf.data.remote.firebase.dao.RemoteReportDao
import com.taxapprf.data.remote.firebase.dao.RemoteTransactionDao
import com.taxapprf.data.remote.firebase.dao.RemoteUserDao
import com.taxapprf.data.sync.SyncAccounts
import com.taxapprf.data.sync.SyncReports
import com.taxapprf.data.sync.SyncTransactions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    private const val DB_NAME = "tax_app"
    //private const val DB_FILE_NAME = "$DB_NAME.db"

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, LocalDatabase::class.java, DB_NAME)
            //.createFromAsset(DB_FILE_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun provideMainDao(db: LocalDatabase) =
        db.mainDao()

    @Singleton
    @Provides
    fun provideSyncDao(db: LocalDatabase) =
        db.syncDao()

    @Singleton
    @Provides
    fun provideReportsDao(db: LocalDatabase) =
        db.reportsDao()

    @Singleton
    @Provides
    fun provideTransactionsDao(db: LocalDatabase) =
        db.transactionsDao()

    @Singleton
    @Provides
    fun provideTransactionDetailDao(db: LocalDatabase) =
        db.transactionDetailDao()


    @Singleton
    @Provides
    fun provideCurrencyDao(db: LocalDatabase) =
        db.currencyDao()

    @Singleton
    @Provides
    fun provideDeletedDao(db: LocalDatabase) =
        db.deletedDao()

    @Singleton
    @Provides
    fun provideExcelDao(db: LocalDatabase) =
        db.excelDao()

    @Singleton
    @Provides
    fun provideTaxDao(db: LocalDatabase) =
        db.taxDao()

    @Singleton
    @Provides
    fun provideConnectivityManager(@ApplicationContext context: Context) =
        context.getSystemService(ConnectivityManager::class.java) as ConnectivityManager

    @Singleton
    @Provides
    fun provideNetworkManager(connectivityManager: ConnectivityManager) =
        NetworkManager(connectivityManager)

    @Singleton
    @Provides
    fun provideSyncAccount(
        localSyncDao: LocalSyncDao,
        remoteAccountDao: RemoteAccountDao
    ) =
        SyncAccounts(localSyncDao, remoteAccountDao)

    @Singleton
    @Provides
    fun provideSyncReport(
        localSyncDao: LocalSyncDao,
        remoteReportDao: RemoteReportDao
    ) =
        SyncReports(localSyncDao, remoteReportDao)

    @Singleton
    @Provides
    fun provideSyncTransaction(
        localSyncDao: LocalSyncDao,
        remoteTransactionDao: RemoteTransactionDao,
    ) =
        SyncTransactions(localSyncDao, remoteTransactionDao)

    @Singleton
    @Provides
    fun provideHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .retryOnConnectionFailure(false)
            .build()

    @Singleton
    @Provides
    fun provideRetrofit(httpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://www.cbr.ru/scripts/")
            .client(httpClient)
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .build()

    @Singleton
    @Provides
    fun provideRemoteCurrencyDao(retrofit: Retrofit): RemoteCurrencyDao =
        retrofit.create(RemoteCurrencyDao::class.java)

    @Singleton
    @Provides
    fun provideFirebase() = Firebase()

    @Singleton
    @Provides
    fun provideFirebaseUserDao(firebaseAPI: Firebase): RemoteUserDao =
        FirebaseUserDaoImpl(firebaseAPI)

    @Singleton
    @Provides
    fun provideFirebaseAccountDao(firebaseAPI: Firebase): RemoteAccountDao =
        FirebaseAccountDaoImpl(firebaseAPI)

    @Singleton
    @Provides
    fun provideFirebaseReportDao(firebaseAPI: Firebase): RemoteReportDao =
        FirebaseReportDaoImpl(firebaseAPI)

    @Singleton
    @Provides
    fun provideFirebaseTransactionDao(firebaseAPI: Firebase): RemoteTransactionDao =
        FirebaseTransactionDaoImpl(firebaseAPI)
}