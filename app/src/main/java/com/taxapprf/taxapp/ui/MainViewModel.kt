package com.taxapprf.taxapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.domain.account.AccountModel
import com.taxapprf.domain.account.GetAccountsUseCase
import com.taxapprf.domain.user.GetUserUseCase
import com.taxapprf.domain.user.IsSignInUseCase
import com.taxapprf.domain.user.UserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val isSignInUseCase: IsSignInUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val getAccountsUseCase: GetAccountsUseCase,
) : ViewModel() {
    // TODO перенести сюда состояния, для подключения. Нет индикации загрузки при заходе уже авторизованным при медленном инете
    private val _state = MutableLiveData<ActivityBaseState>()
    val state: LiveData<ActivityBaseState> = _state

    val isSignIn
        get() = isSignInUseCase.execute()

    val accounts = getAccountsUseCase.execute()
        .flowOn(Dispatchers.IO)
        .onEach { accounts ->
            println(accounts)
            accounts.find { it.active }?.let { _account.postValue(it) }
        }
        .asLiveData(viewModelScope.coroutineContext)

    private val _account = MutableLiveData<AccountModel>()
    val account: LiveData<AccountModel> = _account

    var user: UserModel? = null
    var report: ReportModel? = null
    var transaction: TransactionModel? = null

    fun loadUser() = viewModelScope.launch(Dispatchers.IO) {
        getUserUseCase.execute()
            .collectLatest {

            }
    }

    fun loadAccounts() = viewModelScope.launch(Dispatchers.IO) {
        getAccountsUseCase.execute()
            .onStart {
                _state.postValue(ActivityBaseState.Loading)
            }
            .catch { }
            .collectLatest {
                if (it.isNotEmpty()) {
                    _state.postValue(ActivityBaseState.Success)
                }
            }
    }
}