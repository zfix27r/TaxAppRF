package com.taxapprf.taxapp.ui.account.change

import androidx.lifecycle.viewModelScope
import com.taxapprf.data.error.InputErrorEmailEmpty
import com.taxapprf.domain.account.AccountModel
import com.taxapprf.domain.account.GetAccountsUseCase
import com.taxapprf.domain.account.SaveAccountUseCase
import com.taxapprf.taxapp.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountChangeViewModel @Inject constructor(
    private val getAccountsNameUseCase: GetAccountsUseCase,
    private val saveAccountUseCase: SaveAccountUseCase,
) : BaseViewModel() {
    fun save(accountName: String) {
        if (accountName.isErrorInputAccountChecker()) return

        val accountModel = AccountModel(accountName, true)

        viewModelScope.launch(Dispatchers.IO) {
            saveAccountUseCase.execute(accountModel)
                .onStart { loading() }
                .catch { error(it) }
                .collectLatest { success() }
        }
    }

    private fun String.isErrorInputAccountChecker(): Boolean {
        if (isEmpty()) error(InputErrorEmailEmpty())
        else return false

        return true
    }
}