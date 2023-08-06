package com.taxapprf.data.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.database.DatabaseReference
import com.taxapprf.data.local.room.AppDatabase
import com.taxapprf.data.remote.cbrapi.CBRAPI
import com.taxapprf.data.remote.firebase.FirebaseAPI
import com.taxapprf.data.remote.firebase.FirebaseAccountDaoImpl
import com.taxapprf.data.remote.firebase.FirebaseReportDaoImpl
import com.taxapprf.data.remote.firebase.FirebaseUserDaoImpl
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

    @Provides
    @Singleton
    fun provideHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .retryOnConnectionFailure(false)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(httpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://www.cbr.ru/scripts/")
            .client(httpClient)
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideCBRAPI(retrofit: Retrofit): CBRAPI {
        return retrofit.create(CBRAPI::class.java)
    }

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME).build()

    @Singleton
    @Provides
    fun provideUserDao(db: AppDatabase) = db.userDao()

    @Singleton
    @Provides
    fun provideAccountDao(db: AppDatabase) = db.accountDao()

    @Singleton
    @Provides
    fun provideTaxDao(db: AppDatabase) = db.taxDao()

    @Singleton
    @Provides
    fun provideTransactionDao(db: AppDatabase) = db.transactionDao()

    @Singleton
    @Provides
    fun provideFirebaseAPI() = FirebaseAPI()

    @Singleton
    @Provides
    fun provideFirebaseUserDao(firebaseAPI: FirebaseAPI) = FirebaseUserDaoImpl(firebaseAPI)

    @Singleton
    @Provides
    fun provideFirebaseAccountDao(firebaseAPI: FirebaseAPI) = FirebaseAccountDaoImpl(firebaseAPI)

    @Singleton
    @Provides
    fun provideFirebaseReportDao(firebaseAPI: FirebaseAPI) = FirebaseReportDaoImpl(firebaseAPI)
}