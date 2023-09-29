package com.taxapprf.taxapp.di

import android.content.Context
import com.taxapprf.data.CBRRepositoryImpl
import com.taxapprf.data.ExcelRepositoryImpl
import com.taxapprf.data.UserRepositoryImpl
import com.taxapprf.data.ReportRepositoryImpl
import com.taxapprf.data.SyncRepositoryImpl
import com.taxapprf.data.TransactionRepositoryImpl
import com.taxapprf.domain.user.SwitchAccountUseCase
import com.taxapprf.domain.cbr.GetCBRRatesUseCase
import com.taxapprf.domain.cbr.GetCurrenciesUseCase
import com.taxapprf.domain.delete.DeleteReportWithTransactionsUseCase
import com.taxapprf.domain.report.ObserveReportsUseCase
import com.taxapprf.domain.excel.ImportExcelUseCase
import com.taxapprf.domain.delete.DeleteTransactionWithReportUseCase
import com.taxapprf.domain.transaction.ObserveTransactionsUseCase
import com.taxapprf.domain.transaction.SaveTransactionUseCase
import com.taxapprf.domain.report.ObserveReportUseCase
import com.taxapprf.domain.sync.SyncAllUseCase
import com.taxapprf.domain.excel.ExportExcelUseCase
import com.taxapprf.domain.transaction.ObserveTransactionUseCase
import com.taxapprf.domain.transaction.TransactionType
import com.taxapprf.domain.update.UpdateReportWithTransactionTaxUseCase
import com.taxapprf.domain.user.ObserveUserUseCase
import com.taxapprf.domain.user.SignInUseCase
import com.taxapprf.domain.user.SignOutUseCase
import com.taxapprf.domain.user.SignUpUseCase
import com.taxapprf.taxapp.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

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
    fun provideObserveTransactionsUseCase(transactionRepositoryImpl: TransactionRepositoryImpl) =
        ObserveTransactionsUseCase(transactionRepositoryImpl)

    @Provides
    fun provideObserveTransactionUseCase(transactionRepositoryImpl: TransactionRepositoryImpl) =
        ObserveTransactionUseCase(transactionRepositoryImpl)

    @Provides
    fun provideSaveTransactionUseCase(
        reportRepositoryImpl: ReportRepositoryImpl,
        transactionRepositoryImpl: TransactionRepositoryImpl,
        updateTaxUseCase: UpdateReportWithTransactionTaxUseCase,
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
        UpdateReportWithTransactionTaxUseCase(
            reportRepositoryImpl,
            transactionRepositoryImpl,
            currencyRepositoryImpl
        )

    @Provides
    fun provideDeleteTransactionUseCase(
        transactionRepositoryImpl: TransactionRepositoryImpl,
        reportRepositoryImpl: ReportRepositoryImpl,
    ) =
        DeleteTransactionWithReportUseCase(transactionRepositoryImpl, reportRepositoryImpl)

    @Provides
    fun provideObserveReportUseCase(repositoryImpl: ReportRepositoryImpl) =
        ObserveReportUseCase(repositoryImpl)

    @Provides
    fun provideObserveReportsUseCase(repositoryImpl: ReportRepositoryImpl) =
        ObserveReportsUseCase(repositoryImpl)

    @Provides
    fun provideDeleteReportUseCase(
        repositoryImpl: ReportRepositoryImpl,
        transactionRepositoryImpl: TransactionRepositoryImpl
    ) =
        DeleteReportWithTransactionsUseCase(repositoryImpl, transactionRepositoryImpl)

    @Provides
    fun provideImportExcelUseCase(
        excelRepositoryImpl: ExcelRepositoryImpl,
        saveTransactionUseCase: SaveTransactionUseCase
    ) =
        ImportExcelUseCase(excelRepositoryImpl, saveTransactionUseCase)

    @Provides
    fun provideExportExcelUseCase(
        reportRepositoryImpl: ReportRepositoryImpl,
        transactionRepositoryImpl: TransactionRepositoryImpl,
        excelRepositoryImpl: ExcelRepositoryImpl
    ) =
        ExportExcelUseCase(
            reportRepositoryImpl,
            transactionRepositoryImpl,
            excelRepositoryImpl
        )

}