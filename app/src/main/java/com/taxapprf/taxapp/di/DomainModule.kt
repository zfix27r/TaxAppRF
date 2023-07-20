package com.taxapprf.taxapp.di

import com.taxapprf.data.ActivityRepositoryImpl
import com.taxapprf.domain.activity.GetAccountModelUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object DomainModule {
    @Provides
    fun provideGetUserModelUseCase(repositoryImpl: ActivityRepositoryImpl) =
        GetAccountModelUseCase(repositoryImpl)
}