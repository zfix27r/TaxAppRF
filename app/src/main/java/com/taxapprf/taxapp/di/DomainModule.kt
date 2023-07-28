package com.taxapprf.taxapp.di

import com.taxapprf.data.ActivityRepositoryImpl
import com.taxapprf.data.TaxesRepositoryImpl
import com.taxapprf.data.TransactionRepositoryImpl
import com.taxapprf.domain.user.SignInUseCase
import com.taxapprf.domain.user.IsSignInUseCase
import com.taxapprf.domain.account.GetAccountsNameUseCase
import com.taxapprf.domain.user.SignOutUseCase
import com.taxapprf.domain.user.SignUpUseCase
import com.taxapprf.domain.account.SetActiveAccountUseCase
import com.taxapprf.domain.cbr.GetRateCentralBankUseCase
import com.taxapprf.domain.taxes.GetTaxesUseCase
import com.taxapprf.domain.taxes.SaveTaxesFromExcel
import com.taxapprf.domain.transaction.DeleteTransactionUseCase
import com.taxapprf.domain.year.DeleteYearSumUseCase
import com.taxapprf.domain.transaction.GetTransactionUseCase
import com.taxapprf.domain.transaction.GetTransactionsUseCase
import com.taxapprf.domain.year.GetYearSumUseCase
import com.taxapprf.domain.transaction.SaveTransactionUseCase
import com.taxapprf.domain.year.SaveYearSumUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object DomainModule {
    @Provides
    fun provideIsSignInUseCase(repositoryImpl: ActivityRepositoryImpl) =
        IsSignInUseCase(repositoryImpl)

    @Provides
    fun provideSignInUseCase(repositoryImpl: ActivityRepositoryImpl) =
        SignInUseCase(repositoryImpl)

    @Provides
    fun provideSignUpUseCase(repositoryImpl: ActivityRepositoryImpl) =
        SignUpUseCase(repositoryImpl)

    @Provides
    fun provideSignOutUseCase(repositoryImpl: ActivityRepositoryImpl) =
        SignOutUseCase(repositoryImpl)

    @Provides
    fun provideGetAccountsNameUseCase(repositoryImpl: ActivityRepositoryImpl) =
        GetAccountsNameUseCase(repositoryImpl)

    @Provides
    fun provideSetActiveAccountUseCase(repositoryImpl: ActivityRepositoryImpl) =
        SetActiveAccountUseCase(repositoryImpl)

    @Provides
    fun provideGetRateCentralBankUseCase(repositoryImpl: TaxesRepositoryImpl) =
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
    fun provideGetTaxesUseCase(repositoryImpl: TaxesRepositoryImpl) =
        GetTaxesUseCase(repositoryImpl)

    @Provides
    fun provideSaveTaxesFromExcel(repositoryImpl: TaxesRepositoryImpl) =
        SaveTaxesFromExcel(repositoryImpl)

    @Provides
    fun provideSaveYearSumUseCase(repositoryImpl: TransactionRepositoryImpl) =
        SaveYearSumUseCase(repositoryImpl)

    @Provides
    fun provideDeleteYearSumUseCase(repositoryImpl: TransactionRepositoryImpl) =
        DeleteYearSumUseCase(repositoryImpl)
}