package com.taxapprf.taxapp.ui.account.select

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.taxapprf.domain.user.GetAccountsUseCase
import com.taxapprf.domain.user.SaveAccountModel
import com.taxapprf.domain.user.SaveAccountUseCase
import com.taxapprf.domain.user.SignOutUseCase
import com.taxapprf.taxapp.ui.BaseState
import com.taxapprf.taxapp.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountSelectViewModel @Inject constructor(
    getAccountsUseCase: GetAccountsUseCase,
    private val saveAccountUseCase: SaveAccountUseCase,
    private val signOutUseCase: SignOutUseCase,
) : BaseViewModel() {
    val accounts = getAccountsUseCase.execute().flowOn(Dispatchers.IO)
        .asLiveData(viewModelScope.coroutineContext)

    fun logOut() = viewModelScope.launch(Dispatchers.IO) {
        signOutUseCase.execute()
            .onStart { loading() }
            .catch { error(it) }
            .collectLatest { success(BaseState.LogOut) }
    }

    fun saveAccount(accountName: String) = viewModelScope.launch(Dispatchers.IO) {
        val accountModel = SaveAccountModel("", accountName)
        saveAccountUseCase.execute(accountModel)
            .onStart { loading() }
            .catch { error(it) }
            .collectLatest { success(BaseState.AccountSelect) }
    }
}