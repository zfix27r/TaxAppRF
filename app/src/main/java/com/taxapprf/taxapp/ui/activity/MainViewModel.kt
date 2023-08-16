package com.taxapprf.taxapp.ui.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taxapprf.domain.account.AccountModel
import com.taxapprf.domain.account.GetAccountsUseCase
import com.taxapprf.domain.account.SwitchAccountModel
import com.taxapprf.domain.account.SwitchAccountUseCase
import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.transaction.SaveTransactionUseCase
import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.domain.user.GetUserUseCase
import com.taxapprf.domain.user.IsSignInUseCase
import com.taxapprf.domain.user.SignOutUseCase
import com.taxapprf.domain.user.UserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val isSignInUseCase: IsSignInUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val getAccountsUseCase: GetAccountsUseCase,
    private val switchAccountUseCase: SwitchAccountUseCase,
    val signOutUseCase: SignOutUseCase,
    private val saveTransactionUseCase: SaveTransactionUseCase,
) : ViewModel() {
    private val _state = ActivityStateLiveData()
    val state: LiveData<ActivityBaseState> = _state

    val isSignIn
        get() = isSignInUseCase.execute()

    private val _user = MutableLiveData<UserModel>()
    val user: LiveData<UserModel> = _user

    private val _accounts = MutableLiveData<List<AccountModel>>()
    val accounts: LiveData<List<AccountModel>> = _accounts

    private val _account = MutableLiveData<AccountModel>()
    val account: LiveData<AccountModel> = _account

    var report: ReportModel? = null
    var transaction: TransactionModel? = null

    fun loading() {
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

    fun switchAccount(newAccountName: String) = viewModelScope.launch(Dispatchers.IO) {
        _account.value?.let { oldAccountModel ->
            val switchAccountModel = SwitchAccountModel(oldAccountModel.name, newAccountName)
            switchAccountUseCase.execute(switchAccountModel)
                .onStart { _state.loading() }
                .collectLatest { _state.success() }
        }
    }

    private fun Result<List<AccountModel>>.updateAccounts() {
        exceptionOrNull()
            ?.let { _state.error(it) }
            ?: run {
                getOrNull()?.let { accounts ->
                    if (accounts.isNotEmpty()) {
                        accounts.find { it.active }?.let {
                            _accounts.postValue(accounts)
                            _account.postValue(it)
                        } ?: run {
                            val accountsWithActiveAccount = accounts.setActiveAccount()
                            _accounts.postValue(accountsWithActiveAccount)
                            _account.postValue(accountsWithActiveAccount.first())
                        }
                    } else {
                        listOf<List<AccountModel>>()
                    }
                } ?: run { listOf<List<AccountModel>>() }
            }
    }

    private fun List<AccountModel>.setActiveAccount() = mapIndexed { index, accountModel ->
        if (index == 0) accountModel.copy(active = true)
        else accountModel
    }

    fun signOut() = viewModelScope.launch(Dispatchers.IO) {
        signOutUseCase.execute()
            .onStart { loading() }
            .catch { error(it) }
            .collectLatest { _state.signOut() }
    }

    fun saveTransaction(saveTransactionModel: SaveTransactionModel) =
        viewModelScope.launch(Dispatchers.IO) {
            saveTransactionUseCase.execute(saveTransactionModel)
                .onStart { _state.loading() }
                .catch { _state.error(it) }
                .collectLatest { _state.success() }
        }
}