package com.taxapprf.taxapp.ui.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taxapprf.domain.account.AccountModel
import com.taxapprf.domain.account.GetAccountsUseCase
import com.taxapprf.domain.account.ObserveAccountsUseCase
import com.taxapprf.domain.account.SwitchAccountModel
import com.taxapprf.domain.account.SwitchAccountUseCase
import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.sync.SyncAllUseCase
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.transaction.SaveTransactionUseCase
import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.domain.user.GetUserUseCase
import com.taxapprf.domain.user.IsSignInUseCase
import com.taxapprf.domain.user.SignOutUseCase
import com.taxapprf.domain.user.UserModel
import com.taxapprf.taxapp.ui.BaseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val isSignInUseCase: IsSignInUseCase,
    private val getUserUseCase: GetUserUseCase,
    observeAccountsUseCase: ObserveAccountsUseCase,
    private val getAccountsUseCase: GetAccountsUseCase,
    private val switchAccountUseCase: SwitchAccountUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val saveTransactionUseCase: SaveTransactionUseCase,
    syncAllUseCase: SyncAllUseCase
) : ViewModel() {
    private val _state = ActivityStateLiveData()
    val state: LiveData<BaseState> = _state

    val isSignIn
        get() = isSignInUseCase.execute()

    private val _user = MutableLiveData<UserModel>()
    val user: LiveData<UserModel> = _user

    private val _accounts = MutableLiveData<List<AccountModel>>()
    val accounts: LiveData<List<AccountModel>> = _accounts

    private val _account = MutableLiveData<AccountModel?>()
    val account: LiveData<AccountModel?> = _account

    var report: ReportModel? = null
    var transaction: TransactionModel? = null


    init {
        observeAccountsUseCase.execute()
    }

    val user1 = getUserUseCase.execute().showLoading()

    val account1 = getAccountsUseCase.execute().showLoading()

    val sync = syncAllUseCase.execute()

    fun showLoading() {
        viewModelScope.launch(Dispatchers.IO) {
            getAccountsUseCase.execute()
                .onStart { _state.loading() }
                .catch { _state.error(it) }
                .collectLatest {
                    it.updateAccounts()
                    _state.success()
                }
        }

        viewModelScope.launch(Dispatchers.IO) {
            getUserUseCase.execute()
                .collectLatest { _userModel ->
                    _userModel?.let {
                        _user.postValue(it)
                    }
                }
        }
    }

    fun switchAccount(accountModel: AccountModel) = viewModelScope.launch(Dispatchers.IO) {
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

    fun signOut() = viewModelScope.launch(Dispatchers.IO) {
        signOutUseCase.execute()
            .onStart { showLoading() }
            .catch { error(it) }
            .collectLatest { _state.signOut() }
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