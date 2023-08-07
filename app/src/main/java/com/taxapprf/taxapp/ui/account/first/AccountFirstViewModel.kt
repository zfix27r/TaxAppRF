package com.taxapprf.taxapp.ui.account.first

import androidx.lifecycle.viewModelScope
import com.taxapprf.domain.user.SaveAccountModel
import com.taxapprf.domain.user.SaveAccountUseCase
import com.taxapprf.taxapp.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountFirstViewModel @Inject constructor(
    private val saveAccountUseCase: SaveAccountUseCase,
) : BaseViewModel() {
    fun saveAccount(accountName: String, defaultAccountName: String) {
        val name = accountName.ifEmpty { defaultAccountName }
        val accountModel = SaveAccountModel("", name)

        viewModelScope.launch(Dispatchers.IO) {
            saveAccountUseCase.execute(accountModel)
                .onStart { loading() }
                .catch { error(it) }
                .collectLatest { success() }
        }
    }
}