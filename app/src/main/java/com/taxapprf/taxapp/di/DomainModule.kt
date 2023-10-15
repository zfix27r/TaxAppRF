package com.taxapprf.taxapp.di

import com.taxapprf.data.CurrencyRepositoryImpl
import com.taxapprf.data.DeletedRepositoryImpl
import com.taxapprf.data.ExcelRepositoryImpl
import com.taxapprf.data.MainRepositoryImpl
import com.taxapprf.data.ReportsRepositoryImpl
import com.taxapprf.data.SyncRepositoryImpl
import com.taxapprf.data.TaxRepositoryImpl
import com.taxapprf.data.TransactionDetailRepositoryImpl
import com.taxapprf.data.TransactionsRepositoryImpl
import com.taxapprf.domain.currency.GetCurrencyRateModelsUseCase
import com.taxapprf.domain.excel.ExportExcelUseCase
import com.taxapprf.domain.excel.ImportExcelUseCase
import com.taxapprf.domain.main.account.SwitchAccountUseCase
import com.taxapprf.domain.main.transaction.SaveTransactionUseCase
import com.taxapprf.domain.main.user.ObserveUserWithAccountsUseCase
import com.taxapprf.domain.main.user.SignInUseCase
import com.taxapprf.domain.main.user.SignOutUseCase
import com.taxapprf.domain.main.user.SignUpUseCase
import com.taxapprf.domain.reports.DeleteReportsUseCase
import com.taxapprf.domain.reports.ObserveReportsUseCase
import com.taxapprf.domain.sync.SyncAllUseCase
import com.taxapprf.domain.tax.UpdateAllEmptySumUseCase
import com.taxapprf.domain.tax.UpdateAllEmptyTaxUseCase
import com.taxapprf.domain.transactions.DeleteTransactionsUseCase
import com.taxapprf.domain.transactions.ObserveReportUseCase
import com.taxapprf.domain.transactions.ObserveTransactionsUseCase
import com.taxapprf.domain.transactions.detail.GetTransactionDetailUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object DomainModule {
    /* Main repository */
    @Provides
    fun provideSignInUseCase(mainRepositoryImpl: MainRepositoryImpl) =
        SignInUseCase(mainRepositoryImpl)

    @Provides
    fun provideSignUpUseCase(mainRepositoryImpl: MainRepositoryImpl) =
        SignUpUseCase(mainRepositoryImpl)

    @Provides
    fun provideSignOutUseCase(mainRepositoryImpl: MainRepositoryImpl) =
        SignOutUseCase(mainRepositoryImpl)

    @Provides
    fun provideGetUserUseCase(mainRepositoryImpl: MainRepositoryImpl) =
        ObserveUserWithAccountsUseCase(mainRepositoryImpl)

    @Provides
    fun provideSwitchAccountUseCase(mainRepositoryImpl: MainRepositoryImpl) =
        SwitchAccountUseCase(mainRepositoryImpl)

    @Provides
    fun provideSaveTransactionUseCase(
        mainRepositoryImpl: MainRepositoryImpl,
        updateAllEmptySumUseCase: UpdateAllEmptySumUseCase,
        updateAllEmptyTaxUseCase: UpdateAllEmptyTaxUseCase
    ) =
        SaveTransactionUseCase(
            mainRepositoryImpl,
            updateAllEmptySumUseCase,
            updateAllEmptyTaxUseCase
        )


    /* Sync repository */
    @Provides
    fun provideSyncAllUseCase(syncRepositoryImpl: SyncRepositoryImpl) =
        SyncAllUseCase(syncRepositoryImpl)


    /* Currency repository */
    @Provides
    fun provideGetCurrencyRateTodayFromCBRUseCase(currencyRepositoryImpl: CurrencyRepositoryImpl) =
        GetCurrencyRateModelsUseCase(currencyRepositoryImpl)


    /* Reports repository */
    @Provides
    fun provideObserveReportsUseCase(reportsRepositoryImpl: ReportsRepositoryImpl) =
        ObserveReportsUseCase(reportsRepositoryImpl)


    /* Transactions repository */
    @Provides
    fun provideObserveReportUseCase(transactionsRepositoryImpl: TransactionsRepositoryImpl) =
        ObserveReportUseCase(transactionsRepositoryImpl)

    @Provides
    fun provideObserveTransactionsUseCase(transactionsRepositoryImpl: TransactionsRepositoryImpl) =
        ObserveTransactionsUseCase(transactionsRepositoryImpl)


    /* Transaction detail repository*/
    @Provides
    fun provideGetTransactionDetailUseCase(transactionDetailRepositoryImpl: TransactionDetailRepositoryImpl) =
        GetTransactionDetailUseCase(transactionDetailRepositoryImpl)


    /* Excel repository */
    @Provides
    fun provideImportExcelUseCase(
        excelRepositoryImpl: ExcelRepositoryImpl,
        saveTransactionUseCase: SaveTransactionUseCase
    ) =
        ImportExcelUseCase(excelRepositoryImpl, saveTransactionUseCase)

    @Provides
    fun provideExportExcelUseCase(excelRepositoryImpl: ExcelRepositoryImpl) =
        ExportExcelUseCase(excelRepositoryImpl)


    /* Deleted repository */
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


    /* Tax repository */
    @Provides
    fun provideUpdateAllEmptySumUseCase(taxRepositoryImpl: TaxRepositoryImpl) =
        UpdateAllEmptySumUseCase(taxRepositoryImpl)

    @Provides
    fun provideUpdateAllEmptyTaxUseCase(taxRepositoryImpl: TaxRepositoryImpl) =
        UpdateAllEmptyTaxUseCase(taxRepositoryImpl)
}