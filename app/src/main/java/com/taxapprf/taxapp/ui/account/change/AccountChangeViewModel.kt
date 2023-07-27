package com.taxapprf.taxapp.ui.account.change

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.taxapprf.data.error.InputErrorEmailEmpty
import com.taxapprf.domain.account.GetAccountsNameUseCase
import com.taxapprf.domain.account.SetActiveAccountUseCase
import com.taxapprf.taxapp.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class AccountChangeViewModel @Inject constructor(
    private val getAccountsNameUseCase: GetAccountsNameUseCase,
    private val setActiveAccountUseCase: SetActiveAccountUseCase,
) : BaseViewModel() {
    val accounts = getAccountsNameUseCase.execute().asLiveData(viewModelScope.coroutineContext)

    fun save(account: String) {
/*        if (account.isErrorInputAccountChecker()) return

        viewModelScope.launch(Dispatchers.IO) {
            setActiveAccountUseCase.execute(account)
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