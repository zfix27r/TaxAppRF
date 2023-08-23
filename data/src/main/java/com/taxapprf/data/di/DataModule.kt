package com.taxapprf.data.di

import com.taxapprf.data.remote.cbrapi.CBRAPI
import com.taxapprf.data.remote.firebase.FirebaseAPI
import com.taxapprf.data.remote.firebase.FirebaseAccountDaoImpl
import com.taxapprf.data.remote.firebase.FirebaseReportDaoImpl
import com.taxapprf.data.remote.firebase.FirebaseUserDaoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
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