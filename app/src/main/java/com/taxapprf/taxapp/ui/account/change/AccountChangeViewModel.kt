package com.taxapprf.taxapp.ui.account.change

import androidx.lifecycle.viewModelScope
import com.taxapprf.data.error.InputErrorEmailEmpty
import com.taxapprf.domain.account.AccountModel
import com.taxapprf.domain.account.ChangeAccountUseCase
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
    private val changeAccountUseCase: ChangeAccountUseCase,
) : BaseViewModel() {

    lateinit var accounts: List<AccountModel>

    fun changeAccount(newAccountName: String) {
        if (newAccountName.isErrorInputAccountChecker()) return

        var oldAccountModel: AccountModel? = null
        var newAccountModel: AccountModel? = null

        if (accounts.size > 1) {
            accounts.map { account ->
                if (account.active)
                    oldAccountModel = account
                else if (account.name == newAccountName)
                    newAccountModel = account
            }
        }

        if (oldAccountModel != null && newAccountModel != null) {
            viewModelScope.launch(Dispatchers.IO) {
                changeAccountUseCase.execute(oldAccountModel!!, newAccountModel!!)
                    .onStart { loading() }
                    .catch { error(it) }
                    .collectLatest {
                        success()
                    }
            }
        }
    }

    private fun String.isErrorInputAccountChecker(): Boolean {
        if (isEmpty()) error(InputErrorEmailEmpty())
        else return false

        return true
    }
}