package com.taxapprf.data.di

import android.content.Context
import android.net.ConnectivityManager
import androidx.room.Room
import com.taxapprf.data.NetworkManager
import com.taxapprf.data.local.room.LocalDatabase
import com.taxapprf.data.remote.cbr.RemoteCBRDao
import com.taxapprf.data.remote.firebase.FirebaseAPI
import com.taxapprf.data.remote.firebase.FirebaseAccountDaoImpl
import com.taxapprf.data.remote.firebase.FirebaseReportDaoImpl
import com.taxapprf.data.remote.firebase.FirebaseUserDaoImpl
import com.taxapprf.data.remote.firebase.dao.RemoteUserDao
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
    private const val DB_FILE_NAME = "$DB_NAME.db"

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, LocalDatabase::class.java, DB_NAME)
            .createFromAsset(DB_FILE_NAME)
            .build()

    @Singleton
    @Provides
    fun provideUserDao(db: LocalDatabase) =
        db.userDao()

    @Singleton
    @Provides
    fun provideAccountDao(db: LocalDatabase) =
        db.accountDao()

    @Singleton
    @Provides
    fun provideReportDao(db: LocalDatabase) =
        db.reportDao()

    @Singleton
    @Provides
    fun provideTransactionDao(db: LocalDatabase) =
        db.transactionDao()

    @Singleton
    @Provides
    fun provideCBRDao(db: LocalDatabase) =
        db.cbrDao()

    @Singleton
    @Provides
    fun provideDeletedKeyDao(db: LocalDatabase) =
        db.deletedKeyDao()

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
    fun provideRemoteCBRDao(retrofit: Retrofit): RemoteCBRDao =
        retrofit.create(RemoteCBRDao::class.java)

    @Singleton
    @Provides
    fun provideFirebaseAPI() = FirebaseAPI()

    @Singleton
    @Provides
    fun provideFirebaseUserDao(firebaseAPI: FirebaseAPI): RemoteUserDao =
        FirebaseUserDaoImpl(firebaseAPI)

    @Singleton
    @Provides
    fun provideFirebaseAccountDao(firebaseAPI: FirebaseAPI) = FirebaseAccountDaoImpl(firebaseAPI)

    @Singleton
    @Provides
    fun provideFirebaseReportDao(firebaseAPI: FirebaseAPI) = FirebaseReportDaoImpl(firebaseAPI)
}