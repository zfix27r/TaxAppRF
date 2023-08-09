package com.taxapprf.taxapp.ui.account.add

import com.taxapprf.data.error.InputErrorEmailEmpty
import com.taxapprf.taxapp.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AccountAddViewModel @Inject constructor(
) : BaseViewModel() {
    fun saveAccount(accountName: String) {
        if (accountName.isErrorInputAccountChecker()) return

/*        val accountModel = SaveAccountModel("", accountName)

        viewModelScope.launch(Dispatchers.IO) {
            saveAccountUseCase.execute(accountModel)
                .onStart { loading() }
                .catch { error(it) }
                .collectLatest { success() }
        }*/
    }

    private fun String.isErrorInputAccountChecker(): Boolean {
        if (isEmpty()) error(InputErrorEmailEmpty())
        else return false

        return true
    }
}