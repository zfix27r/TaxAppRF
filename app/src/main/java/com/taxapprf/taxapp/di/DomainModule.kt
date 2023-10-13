package com.taxapprf.taxapp.di

import com.taxapprf.data.CurrencyRepositoryImpl
import com.taxapprf.data.DeletedRepositoryImpl
import com.taxapprf.data.ExcelRepositoryImpl
import com.taxapprf.data.MainRepositoryImpl
import com.taxapprf.data.ReportRepositoryImpl
import com.taxapprf.data.ReportsRepositoryImpl
import com.taxapprf.data.SyncRepositoryImpl
import com.taxapprf.data.TransactionDetailRepositoryImpl
import com.taxapprf.data.TransactionRepositoryImpl
import com.taxapprf.data.TransactionsRepositoryImpl
import com.taxapprf.data.UserRepositoryImpl
import com.taxapprf.domain.cbr.GetCurrencyRateModelsUseCase
import com.taxapprf.domain.excel.ExportExcelUseCase
import com.taxapprf.domain.excel.ImportExcelUseCase
import com.taxapprf.domain.main.SaveTransactionUseCase
import com.taxapprf.domain.reports.DeleteReportsUseCase
import com.taxapprf.domain.reports.ObserveReportsUseCase
import com.taxapprf.domain.sync.SyncAllUseCase
import com.taxapprf.domain.transaction.ObserveTransactionsUseCase
import com.taxapprf.domain.transaction.detail.GetTransactionDetailUseCase
import com.taxapprf.domain.transactions.DeleteTransactionsUseCase
import com.taxapprf.domain.transactions.ObserveReportUseCase
import com.taxapprf.domain.update.UpdateReportWithTransactionTaxUseCase
import com.taxapprf.domain.user.ObserveUserWithAccountsUseCase
import com.taxapprf.domain.user.SignInUseCase
import com.taxapprf.domain.user.SignOutUseCase
import com.taxapprf.domain.user.SignUpUseCase
import com.taxapprf.domain.user.SwitchAccountUseCase
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
    fun provideSignOutUseCase(
        userRepositoryImpl: UserRepositoryImpl,
        reportRepositoryImpl: ReportRepositoryImpl,
        transactionRepositoryImpl: TransactionRepositoryImpl
    ) =
        SignOutUseCase(userRepositoryImpl, reportRepositoryImpl, transactionRepositoryImpl)

    @Provides
    fun provideGetUserUseCase(userRepositoryImpl: UserRepositoryImpl) =
        ObserveUserWithAccountsUseCase(userRepositoryImpl)

    @Provides
    fun provideSwitchAccountUseCase(userRepositoryImpl: UserRepositoryImpl) =
        SwitchAccountUseCase(userRepositoryImpl)

    @Provides
    fun provideSyncAllUseCase(syncRepositoryImpl: SyncRepositoryImpl) =
        SyncAllUseCase(syncRepositoryImpl)

    @Provides
    fun provideGetCurrencyRateTodayFromCBRUseCase(currencyRepositoryImpl: CurrencyRepositoryImpl) =
        GetCurrencyRateModelsUseCase(currencyRepositoryImpl)

    @Provides
    fun provideObserveTransactionsUseCase(transactionRepositoryImpl: TransactionRepositoryImpl) =
        ObserveTransactionsUseCase(transactionRepositoryImpl)

    @Provides
    fun provideUpdateTaxTransactionUseCase(
        reportRepositoryImpl: ReportRepositoryImpl,
        transactionRepositoryImpl: TransactionRepositoryImpl,
        currencyRepositoryImpl: CurrencyRepositoryImpl
    ) =
        UpdateReportWithTransactionTaxUseCase(
            reportRepositoryImpl,
            transactionRepositoryImpl,
            currencyRepositoryImpl
        )

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

    @Provides
    fun provideSaveTransaction1UseCase(
        mainRepositoryImpl: MainRepositoryImpl,
        currencyRepositoryImpl: CurrencyRepositoryImpl
    ) =
        SaveTransactionUseCase(
            mainRepositoryImpl,
            currencyRepositoryImpl
        )

    // Reports
    @Provides
    fun provideObserveReportsUseCase(reportsRepositoryImpl: ReportsRepositoryImpl) =
        ObserveReportsUseCase(reportsRepositoryImpl)

    // Transactions
    @Provides
    fun provideObserveReportUseCase(transactionsRepositoryImpl: TransactionsRepositoryImpl) =
        ObserveReportUseCase(transactionsRepositoryImpl)

    // TransactionDetail
    @Provides
    fun provideGetTransactionDetailUseCase(transactionDetailRepositoryImpl: TransactionDetailRepositoryImpl) =
        GetTransactionDetailUseCase(transactionDetailRepositoryImpl)

    // Deleted
    @Provides
    fun provideDeleteReportsUseCase(
        deletedRepositoryImpl: DeletedRepositoryImpl,
        syncRepositoryImpl: SyncRepositoryImpl,
    ) =
        DeleteReportsUseCase(deletedRepositoryImpl, syncRepositoryImpl)

    @Provides
    fun provideDeleteTransactionsUseCase(
        deletedRepositoryImpl: DeletedRepositoryImpl,
        syncRepositoryImpl: SyncRepositoryImpl,
    ) =
        DeleteTransactionsUseCase(deletedRepositoryImpl, syncRepositoryImpl)
}