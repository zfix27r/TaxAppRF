package com.taxapprf.taxapp.ui.account.change

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.taxapprf.data.error.InputErrorEmailEmpty
import com.taxapprf.domain.user.GetAccountsUseCase
import com.taxapprf.domain.user.SaveAccountModel
import com.taxapprf.domain.user.SaveAccountUseCase
import com.taxapprf.taxapp.ui.BaseState
import com.taxapprf.taxapp.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountChangeViewModel @Inject constructor(
    getAccountsUseCase: GetAccountsUseCase,
    private val saveAccountUseCase: SaveAccountUseCase,
) : BaseViewModel() {
    val accounts = getAccountsUseCase.execute()
        .onStart { loading() }
        .catch { error(it) }
        .onEach { success() }
        .flowOn(Dispatchers.IO)
        .mapNotNull { accounts ->
            println(accounts)
            accounts.mapIndexed { index, accountModel ->
                if (accountModel.active) _activeAccountPosition = index
                accountModel.name
            }
        }
        .asLiveData(viewModelScope.coroutineContext)
    private var _activeAccountPosition = 0
    val activeAccountPosition
        get() = _activeAccountPosition

    fun saveAccount(accountName: String) {
        if (accountName.isErrorInputAccountChecker()) return

        val accountModel = SaveAccountModel(accountName)

        viewModelScope.launch(Dispatchers.IO) {
            saveAccountUseCase.execute(accountModel)
                .onStart { loading() }
                .catch { error(it) }
                .collectLatest { success(BaseState.SuccessEdit) }
        }
    }

    private fun String.isErrorInputAccountChecker(): Boolean {
        if (isEmpty()) error(InputErrorEmailEmpty())
        else return false

        return true
    }
}