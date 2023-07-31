package com.taxapprf.taxapp.ui.account.select

import androidx.lifecycle.viewModelScope
import com.taxapprf.domain.account.AccountModel
import com.taxapprf.domain.account.SaveAccountUseCase
import com.taxapprf.domain.user.SignOutUseCase
import com.taxapprf.taxapp.ui.BaseState
import com.taxapprf.taxapp.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountSelectViewModel @Inject constructor(
    private val saveAccountUseCase: SaveAccountUseCase,
    private val signOutUseCase: SignOutUseCase,
) : BaseViewModel() {
    fun logOut() = viewModelScope.launch(Dispatchers.IO) {
        signOutUseCase.execute()
            .onStart { loading() }
            .catch { error(it) }
            .collectLatest { success(BaseState.LogOut) }
    }

    fun saveAccount(accountName: String) = viewModelScope.launch(Dispatchers.IO) {
        val accountModel = AccountModel(accountName, true)
        saveAccountUseCase.execute(accountModel)
            .onStart { loading() }
            .catch { error(it) }
            .collectLatest { success(BaseState.AccountSelect) }
    }
}