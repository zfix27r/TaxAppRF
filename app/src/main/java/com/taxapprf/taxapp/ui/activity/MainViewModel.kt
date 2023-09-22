package com.taxapprf.taxapp.ui.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taxapprf.domain.account.AccountModel
import com.taxapprf.domain.account.ObserveAccountsUseCase
import com.taxapprf.domain.account.SwitchAccountUseCase
import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.sync.SyncAllUseCase
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.transaction.SaveTransactionUseCase
import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.domain.user.GetUserUseCase
import com.taxapprf.domain.user.IsSignInUseCase
import com.taxapprf.domain.user.SignOutUseCase
import com.taxapprf.taxapp.ui.BaseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val isSignInUseCase: IsSignInUseCase,
    getUserUseCase: GetUserUseCase,
    observeAccountsUseCase: ObserveAccountsUseCase,
    private val switchAccountUseCase: SwitchAccountUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val saveTransactionUseCase: SaveTransactionUseCase,
    syncAllUseCase: SyncAllUseCase
) : ViewModel() {
    private val _state = ActivityStateLiveData()
    val state: LiveData<BaseState> = _state

    var report: ReportModel? = null
    var transaction: TransactionModel? = null

    val isSignIn
        get() = isSignInUseCase.execute()

    val user =
        getUserUseCase.execute()
            .showLoading()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000L),
                initialValue = null
            )

    val accounts =
        observeAccountsUseCase.execute()
            .showLoading()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000L),
                initialValue = null
            )

    val sync = syncAllUseCase.execute()

    fun switchAccount(accountModel: AccountModel) =
        viewModelScope.launch(Dispatchers.IO) {
            _account.value?.let { oldAccountModel ->
                val switchAccountModel = SwitchAccountModel(
                    oldAccountModel.id, oldAccountModel.accountKey,
                    accountModel.id, accountModel.accountKey
                )
                switchAccountUseCase.execute(switchAccountModel)
            }
        }

    private fun List<AccountModel>.updateAccounts() {
        if (isNotEmpty()) {
            find { it.isActive }?.let {
                _accounts.postValue(this)
                _account.postValue(it)
            } ?: run {
                val accountsWithActiveAccount = setActiveAccount()
                _accounts.postValue(accountsWithActiveAccount)
                _account.postValue(accountsWithActiveAccount.first())
            }
        }
    }

    private fun List<AccountModel>.setActiveAccount() = mapIndexed { index, accountModel ->
        if (index == 0) accountModel.copy(isActive = true)
        else accountModel
    }

    fun signOut() =
        viewModelScope.launch(Dispatchers.IO) {
            signOutUseCase.execute()
        }

    fun saveTransaction(saveTransactionModel: SaveTransactionModel) =
        viewModelScope.launch(Dispatchers.IO) {
            flow { emit(saveTransactionUseCase.execute(saveTransactionModel)) }
                .onStart { _state.loading() }
                .catch { _state.error(it) }
                .collectLatest { _state.success() }
        }

    private fun <T> Flow<T>.showLoading() =
        this
            .onStart { _state.loading() }
            .catch { _state.error(it) }
            .onEach { _state.success() }
}