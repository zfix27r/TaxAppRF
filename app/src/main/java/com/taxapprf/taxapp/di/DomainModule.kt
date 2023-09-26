package com.taxapprf.taxapp.di

import com.taxapprf.data.CBRRepositoryImpl
import com.taxapprf.data.UserRepositoryImpl
import com.taxapprf.data.ReportRepositoryImpl
import com.taxapprf.data.SyncRepositoryImpl
import com.taxapprf.data.TransactionRepositoryImpl
import com.taxapprf.domain.user.SwitchAccountUseCase
import com.taxapprf.domain.cbr.GetCBRRatesUseCase
import com.taxapprf.domain.cbr.GetCurrenciesUseCase
import com.taxapprf.domain.report.DeleteReportWithTransactionsUseCase
import com.taxapprf.domain.report.ObserveReportsUseCase
import com.taxapprf.domain.transaction.SaveTransactionsFromExcelUseCase
import com.taxapprf.domain.transaction.DeleteTransactionUseCase
import com.taxapprf.domain.transaction.ObserveTransactionsUseCase
import com.taxapprf.domain.transaction.SaveTransactionUseCase
import com.taxapprf.domain.report.ObserveReportUseCase
import com.taxapprf.domain.sync.SyncAllUseCase
import com.taxapprf.domain.transaction.GetExcelToShareUseCase
import com.taxapprf.domain.transaction.GetExcelToStorageUseCase
import com.taxapprf.domain.tax.UpdateTaxUseCase
import com.taxapprf.domain.user.ObserveUserUseCase
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
    fun provideSignInUseCase(userRepositoryImpl: UserRepositoryImpl) =
        SignInUseCase(userRepositoryImpl)

    @Provides
    fun provideSignUpUseCase(userRepositoryImpl: UserRepositoryImpl) =
        SignUpUseCase(userRepositoryImpl)

    @Provides
    fun provideSignOutUseCase(userRepositoryImpl: UserRepositoryImpl) =
        SignOutUseCase(userRepositoryImpl)

    @Provides
    fun provideGetUserUseCase(userRepositoryImpl: UserRepositoryImpl) =
        ObserveUserUseCase(userRepositoryImpl)

    @Provides
    fun provideSwitchAccountUseCase(userRepositoryImpl: UserRepositoryImpl) =
        SwitchAccountUseCase(userRepositoryImpl)

    @Provides
    fun provideSyncAllUseCase(syncRepositoryImpl: SyncRepositoryImpl) =
        SyncAllUseCase(syncRepositoryImpl)

    @Provides
    fun provideGetCurrencyRateTodayFromCBRUseCase(currencyRepositoryImpl: CBRRepositoryImpl) =
        GetCBRRatesUseCase(currencyRepositoryImpl)

    @Provides
    fun provideGetTransactionsUseCase(repositoryImpl: TransactionRepositoryImpl) =
        ObserveTransactionsUseCase(repositoryImpl)

    @Provides
    fun provideSaveTransactionUseCase(
        reportRepositoryImpl: ReportRepositoryImpl,
        transactionRepositoryImpl: TransactionRepositoryImpl,
        updateTaxUseCase: UpdateTaxUseCase,
    ) =
        SaveTransactionUseCase(
            reportRepositoryImpl,
            transactionRepositoryImpl,
            updateTaxUseCase
        )

    @Provides
    fun provide(cbrRepositoryImpl: CBRRepositoryImpl) =
        GetCurrenciesUseCase(cbrRepositoryImpl)

    @Provides
    fun provideUpdateTaxTransactionUseCase(
        reportRepositoryImpl: ReportRepositoryImpl,
        transactionRepositoryImpl: TransactionRepositoryImpl,
        currencyRepositoryImpl: CBRRepositoryImpl
    ) =
        UpdateTaxUseCase(
            reportRepositoryImpl,
            transactionRepositoryImpl,
            currencyRepositoryImpl
        )

    @Provides
    fun provideDeleteTransactionUseCase(
        transactionRepositoryImpl: TransactionRepositoryImpl,
        reportRepositoryImpl: ReportRepositoryImpl,
    ) =
        DeleteTransactionUseCase(transactionRepositoryImpl, reportRepositoryImpl)

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