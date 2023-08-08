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
    fun changeAccount(oldAccountModel: AccountModel, newAccountModel: AccountModel) =
        viewModelScope.launch(Dispatchers.IO) {
            changeAccountUseCase.execute(oldAccountModel, newAccountModel)
                .onStart { loading() }
                .catch { error(it) }
                .collectLatest {
                    success()
                }
        }

    fun changeAccount(oldAccountModel: AccountModel, newAccountName: String) {
        if (newAccountName.isErrorInputAccountChecker()) return
        val newAccountModel = AccountModel(newAccountName, true)
        changeAccount(oldAccountModel, newAccountModel)
    }

    private fun String.isErrorInputAccountChecker(): Boolean {
        if (isEmpty()) error(InputErrorEmailEmpty())
        else return false

        return true
    }
}