package com.taxapprf.taxapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.taxapprf.domain.account.GetAccountsUseCase
import com.taxapprf.domain.user.IsSignInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val isSignInUseCase: IsSignInUseCase,
    getAccountsUseCase: GetAccountsUseCase
) : ViewModel() {
    val isSignIn
        get() = isSignInUseCase.execute()

    val accounts = getAccountsUseCase.execute().asLiveData(viewModelScope.coroutineContext)
    val isAccountEmpty
        get() = accounts.value?.isEmpty() ?: true

    val activeAccount
        get() = accounts.value?.find { it.active }

    private var _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    private var _userEmail = MutableLiveData<String>()
    val userEmail: LiveData<String> = _userEmail
}