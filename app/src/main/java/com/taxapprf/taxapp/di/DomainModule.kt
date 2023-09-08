package com.taxapprf.taxapp.di

import com.taxapprf.data.AccountRepositoryImpl
import com.taxapprf.data.CurrencyRepositoryImpl
import com.taxapprf.data.UserRepositoryImpl
import com.taxapprf.data.ReportRepositoryImpl
import com.taxapprf.data.TransactionRepositoryImpl
import com.taxapprf.domain.NetworkManager
import com.taxapprf.domain.account.SwitchAccountUseCase
import com.taxapprf.domain.currency.GetCurrencyRateTodayFromCBRUseCase
import com.taxapprf.domain.report.DeleteReportWithTransactionsUseCase
import com.taxapprf.domain.report.ObserveReportsUseCase
import com.taxapprf.domain.transaction.SaveTransactionsFromExcelUseCase
import com.taxapprf.domain.transaction.DeleteTransactionUseCase
import com.taxapprf.domain.transaction.ObserveTransactionsUseCase
import com.taxapprf.domain.transaction.SaveTransactionUseCase
import com.taxapprf.domain.account.GetAccountsUseCase
import com.taxapprf.domain.report.ObserveReportUseCase
import com.taxapprf.domain.transaction.GetExcelToShareUseCase
import com.taxapprf.domain.transaction.GetExcelToStorageUseCase
import com.taxapprf.domain.transaction.UpdateTaxTransactionUseCase
import com.taxapprf.domain.user.GetUserUseCase
import com.taxapprf.domain.user.IsSignInUseCase
import com.taxapprf.domain.user.SignInUseCase
import com.taxapprf.domain.user.SignOutUseCase
import com.taxapprf.domain.user.SignUpUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object DomainModule {
    @Provides
    fun provideSignInUseCase(repositoryImpl: UserRepositoryImpl) =
        SignInUseCase(repositoryImpl)

    @Provides
    fun provideSignUpUseCase(repositoryImpl: UserRepositoryImpl) =
        SignUpUseCase(repositoryImpl)

    @Provides
    fun provideSignOutUseCase(repositoryImpl: UserRepositoryImpl) =
        SignOutUseCase(repositoryImpl)

    @Provides
    fun provideIsSignInUseCase(repositoryImpl: UserRepositoryImpl) =
        IsSignInUseCase(repositoryImpl)

    @Provides
    fun provideGetUserUseCase(repositoryImpl: UserRepositoryImpl) =
        GetUserUseCase(repositoryImpl)

    @Provides
    fun provideGetAccountsUseCase(repositoryImpl: AccountRepositoryImpl) =
        GetAccountsUseCase(repositoryImpl)

    @Provides
    fun provideSwitchAccountUseCase(repositoryImpl: AccountRepositoryImpl) =
        SwitchAccountUseCase(repositoryImpl)

    @Provides
    fun provideGetCurrencyRateTodayFromCBRUseCase(repositoryImpl: CurrencyRepositoryImpl) =
        GetCurrencyRateTodayFromCBRUseCase(repositoryImpl)

    @Provides
    fun provideGetTransactionsUseCase(repositoryImpl: TransactionRepositoryImpl) =
        ObserveTransactionsUseCase(repositoryImpl)

    @Provides
    fun provideSaveTransactionUseCase(
        networkManager: NetworkManager,
        reportRepositoryImpl: ReportRepositoryImpl,
        transactionRepositoryImpl: TransactionRepositoryImpl,
        currencyRepositoryImpl: CurrencyRepositoryImpl
    ) =
        SaveTransactionUseCase(
            reportRepositoryImpl,
            transactionRepositoryImpl,
        )

    @Provides
    fun provideUpdateTaxTransactionUseCase(
        networkManager: NetworkManager,
        reportRepositoryImpl: ReportRepositoryImpl,
        transactionRepositoryImpl: TransactionRepositoryImpl,
        currencyRepositoryImpl: CurrencyRepositoryImpl
    ) =
        UpdateTaxTransactionUseCase(
            networkManager,
            reportRepositoryImpl,
            transactionRepositoryImpl,
            currencyRepositoryImpl
        )

    @Provides
    fun provideDeleteTransactionUseCase(repositoryImpl: TransactionRepositoryImpl) =
        DeleteTransactionUseCase(repositoryImpl)

    @Provides
    fun provideObserveReportUseCase(repositoryImpl: ReportRepositoryImpl) =
        ObserveReportUseCase(repositoryImpl)

    @Provides
    fun provideObserveReportsUseCase(repositoryImpl: ReportRepositoryImpl) =
        ObserveReportsUseCase(repositoryImpl)

    @Provides
    fun provideSaveTransactionsFromExcelUseCase(repositoryImpl: TransactionRepositoryImpl) =
        SaveTransactionsFromExcelUseCase(repositoryImpl)

    @Provides
    fun provideDeleteReportUseCase(
        repositoryImpl: ReportRepositoryImpl,
        transactionRepositoryImpl: TransactionRepositoryImpl
    ) =
        DeleteReportWithTransactionsUseCase(repositoryImpl, transactionRepositoryImpl)

    @Provides
    fun provideGetExcelToStorageUseCase(transactionRepositoryImpl: TransactionRepositoryImpl) =
        GetExcelToStorageUseCase(transactionRepositoryImpl)

    @Provides
    fun provideGetExcelToShareUseCase(transactionRepositoryImpl: TransactionRepositoryImpl) =
        GetExcelToShareUseCase(transactionRepositoryImpl)
}