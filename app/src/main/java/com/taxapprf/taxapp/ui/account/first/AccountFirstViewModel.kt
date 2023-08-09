package com.taxapprf.taxapp.ui.account.first

import com.taxapprf.taxapp.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AccountFirstViewModel @Inject constructor(
) : BaseViewModel() {
    fun saveAccount(accountName: String, defaultAccountName: String) {
/*        val name = accountName.ifEmpty { defaultAccountName }
        val accountModel = SaveAccountModel("", name)

        viewModelScope.launch(Dispatchers.IO) {
            saveAccountUseCase.execute(accountModel)
                .onStart { loading() }
                .catch { error(it) }
                .collectLatest { success() }
        }*/
    }
}