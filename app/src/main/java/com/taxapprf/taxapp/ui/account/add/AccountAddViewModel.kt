package com.taxapprf.taxapp.ui.account.add

import androidx.lifecycle.viewModelScope
import com.taxapprf.data.error.InputErrorEmailEmpty
import com.taxapprf.domain.account.SetActiveAccountUseCase
import com.taxapprf.taxapp.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountAddViewModel @Inject constructor(
    private val setActiveAccountUseCase: SetActiveAccountUseCase,
) : BaseViewModel() {
    fun save(accountName: String) {
        if (accountName.isErrorInputAccountChecker()) return

        println("@@@@@@@@@@@@ 1")
        viewModelScope.launch(Dispatchers.IO) {
            setActiveAccountUseCase.execute(accountName)
                .onStart {
                    println("@@@@@@@@@@@@ 2")
                    loading() }
                .catch { error(it) }
                .collectLatest {
                    println("@@@@@@@@@@@@ 3")
                    success() }
        }
    }

    private fun String.isErrorInputAccountChecker(): Boolean {
        if (isEmpty()) error(InputErrorEmailEmpty())
        else return false

        return true
    }
}