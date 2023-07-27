package com.taxapprf.taxapp.ui.account.select

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.taxapprf.domain.account.GetAccountsNameUseCase
import com.taxapprf.domain.user.SignOutUseCase
import com.taxapprf.domain.account.SetActiveAccountUseCase
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
    private val getAccountsNameUseCase: GetAccountsNameUseCase,
    private val setActiveAccountUseCase: SetActiveAccountUseCase,
    private val signOutUseCase: SignOutUseCase,
) : BaseViewModel() {
    val accounts = getAccountsNameUseCase.execute().asLiveData(viewModelScope.coroutineContext)

    fun logOut() = viewModelScope.launch(Dispatchers.IO) {
        signOutUseCase.execute()
            .onStart { loading() }
            .catch { error(it) }
            .collectLatest { success(BaseState.LogOut) }
    }

    fun setActiveAccount(accountName: String) = viewModelScope.launch(Dispatchers.IO) {
        setActiveAccountUseCase.execute(accountName)
            .onStart { loading() }
            .catch { error(it) }
            .collectLatest { success(BaseState.AccountSelect) }
    }
}