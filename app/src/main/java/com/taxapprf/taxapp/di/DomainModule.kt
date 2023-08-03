package com.taxapprf.taxapp.di

import com.taxapprf.data.AccountRepositoryImpl
import com.taxapprf.data.SignRepositoryImpl
import com.taxapprf.data.TaxRepositoryImpl
import com.taxapprf.data.TransactionRepositoryImpl
import com.taxapprf.domain.cbr.GetRateCentralBankUseCase
import com.taxapprf.domain.taxes.DeleteTaxUseCase
import com.taxapprf.domain.taxes.GetTaxesUseCase
import com.taxapprf.domain.taxes.SaveTaxesFromExcelUseCase
import com.taxapprf.domain.transaction.DeleteTransactionUseCase
import com.taxapprf.domain.transaction.GetTransactionUseCase
import com.taxapprf.domain.transaction.GetTransactionsUseCase
import com.taxapprf.domain.transaction.SaveTransactionUseCase
import com.taxapprf.domain.user.GetAccountsUseCase
import com.taxapprf.domain.user.GetUserUseCase
import com.taxapprf.domain.user.SaveAccountUseCase
import com.taxapprf.domain.user.SignInUseCase
import com.taxapprf.domain.user.SignOutUseCase
import com.taxapprf.domain.user.SignUpUseCase
import com.taxapprf.domain.year.DeleteYearSumUseCase
import com.taxapprf.domain.year.GetYearSumUseCase
import com.taxapprf.domain.year.SaveYearSumUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object DomainModule {
    @Provides
    fun provideSignInUseCase(repositoryImpl: SignRepositoryImpl) =
        SignInUseCase(repositoryImpl)

    @Provides
    fun provideSignUpUseCase(repositoryImpl: SignRepositoryImpl) =
        SignUpUseCase(repositoryImpl)

    @Provides
    fun provideSignOutUseCase(repositoryImpl: SignRepositoryImpl) =
        SignOutUseCase(repositoryImpl)

    @Provides
    fun provideGetUserUseCase(repositoryImpl: SignRepositoryImpl) =
        GetUserUseCase(repositoryImpl)

    @Provides
    fun provideGetAccountsUseCase(repositoryImpl: AccountRepositoryImpl) =
        GetAccountsUseCase(repositoryImpl)

    @Provides
    fun provideSaveAccountUseCase(repositoryImpl: AccountRepositoryImpl) =
        SaveAccountUseCase(repositoryImpl)

    @Provides
    fun provideGetRateCentralBankUseCase(repositoryImpl: TaxRepositoryImpl) =
        GetRateCentralBankUseCase(repositoryImpl)

    @Provides
    fun provideGetTransactionUseCase(repositoryImpl: TransactionRepositoryImpl) =
        GetTransactionUseCase(repositoryImpl)

    @Provides
    fun provideGetTransactionsUseCase(repositoryImpl: TransactionRepositoryImpl) =
        GetTransactionsUseCase(repositoryImpl)

    @Provides
    fun provideSaveTransactionUseCase(repositoryImpl: TransactionRepositoryImpl) =
        SaveTransactionUseCase(repositoryImpl)

    @Provides
    fun provideDeleteTransactionUseCase(repositoryImpl: TransactionRepositoryImpl) =
        DeleteTransactionUseCase(repositoryImpl)

    @Provides
    fun provideGetYearSumUseCase(repositoryImpl: TransactionRepositoryImpl) =
        GetYearSumUseCase(repositoryImpl)

    @Provides
    fun provideGetTaxesUseCase(repositoryImpl: TaxRepositoryImpl) =
        GetTaxesUseCase(repositoryImpl)

    @Provides
    fun provideSaveTaxesFromExcelUseCase(repositoryImpl: TaxRepositoryImpl) =
        SaveTaxesFromExcelUseCase(repositoryImpl)

    @Provides
    fun provideDeleteTaxUseCase(repositoryImpl: TaxRepositoryImpl) =
        DeleteTaxUseCase(repositoryImpl)

    @Provides
    fun provideSaveYearSumUseCase(repositoryImpl: TransactionRepositoryImpl) =
        SaveYearSumUseCase(repositoryImpl)

    @Provides
    fun provideDeleteYearSumUseCase(repositoryImpl: TransactionRepositoryImpl) =
        DeleteYearSumUseCase(repositoryImpl)
}