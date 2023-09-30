package com.taxapprf.taxapp.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taxapprf.domain.user.AccountModel
import com.taxapprf.domain.user.SwitchAccountUseCase
import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.sync.SyncAllUseCase
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.transaction.SaveTransactionUseCase
import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.domain.update.UpdateReportWithTransactionTaxUseCase
import com.taxapprf.domain.user.ObserveUserUseCase
import com.taxapprf.domain.user.SignOutUseCase
import com.taxapprf.domain.user.SwitchAccountModel
import com.taxapprf.taxapp.ui.makeHot
import com.taxapprf.taxapp.ui.showLoading
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    observeUserUseCase: ObserveUserUseCase,
    private val switchAccountUseCase: SwitchAccountUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val saveTransactionUseCase: SaveTransactionUseCase,
    private val updateTaxTransactionUseCase: UpdateReportWithTransactionTaxUseCase,
    syncAllUseCase: SyncAllUseCase
) : ViewModel() {
    init {
        /*        viewModelScope.launch(Dispatchers.IO) {
                    val date = LocalDate.now().toEpochDay()
                    for (i in 0..900) {
                        getCBRRateUseCase.execute("USD", date - i)
                    }
                }*/
    }

    val userWithAccounts =
        observeUserUseCase.execute()
            .showLoading()
            .makeHot(viewModelScope)

    val sync = syncAllUseCase.execute()

    fun switchAccount(accountName: String) =
        userWithAccounts.value?.user?.id?.let { userId ->
            viewModelScope.launch(Dispatchers.IO) {
                val switchAccountModel =
                    SwitchAccountModel(
                        userId = userId,
                        accountName = accountName
                    )
                switchAccountUseCase.execute(switchAccountModel)
            }
        }

    private fun List<AccountModel>.updateAccounts() {
        /*        if (isNotEmpty()) {
                    find { it.isActive }?.let {
                        _accounts.postValue(this)
                        _account.postValue(it)
                    } ?: run {
                        val accountsWithActiveAccount = setActiveAccount()
                        _accounts.postValue(accountsWithActiveAccount)
                        _account.postValue(accountsWithActiveAccount.first())
                    }
                }*/
    }

    /*
        private fun List<AccountModel>.setActiveAccount() = mapIndexed { index, accountModel ->
            if (index == 0) accountModel.copy(isActive = true)
            else accountModel
        }
    */

    fun signOut() =
        viewModelScope.launch(Dispatchers.IO) {
            signOutUseCase.execute()
        }

    fun saveTransaction(saveTransactionModel: SaveTransactionModel) =
        viewModelScope.launch(Dispatchers.IO) {
            saveTransactionUseCase.execute(saveTransactionModel)
        }
}