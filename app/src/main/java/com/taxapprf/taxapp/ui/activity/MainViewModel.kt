package com.taxapprf.taxapp.ui.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taxapprf.domain.user.AccountModel
import com.taxapprf.domain.user.SwitchAccountUseCase
import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.sync.SyncAllUseCase
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.transaction.SaveTransactionUseCase
import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.domain.tax.UpdateTaxUseCase
import com.taxapprf.domain.user.ObserveUserUseCase
import com.taxapprf.domain.user.SignOutUseCase
import com.taxapprf.taxapp.ui.BaseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    observeUserUseCase: ObserveUserUseCase,
    private val switchAccountUseCase: SwitchAccountUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val saveTransactionUseCase: SaveTransactionUseCase,
    private val updateTaxTransactionUseCase: UpdateTaxUseCase,
    syncAllUseCase: SyncAllUseCase
) : ViewModel() {
    private val _state = ActivityStateLiveData()
    val state: LiveData<BaseState> = _state

    var report: ReportModel? = null
        get() {
            val model = field
            field = null
            return model
        }
    var transaction: TransactionModel? = null
        get() {
            val model = field
            field = null
            return model
        }

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
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000L),
                initialValue = null
            )

    val sync = syncAllUseCase.execute()

    fun switchAccount(accountModel: AccountModel) =
        viewModelScope.launch(Dispatchers.IO) {
/*            _account.value?.let { oldAccountModel ->
                val switchAccountModel = SwitchAccountModel(
                    oldAccountModel.id, oldAccountModel.accountKey,
                    accountModel.id, accountModel.accountKey
                )
                switchAccountUseCase.execute(switchAccountModel)
            }*/
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

    private fun <T> Flow<T>.showLoading() =
        this
            .onStart { _state.loading() }
            .catch { _state.error(it) }
            .onEach { _state.success() }
}