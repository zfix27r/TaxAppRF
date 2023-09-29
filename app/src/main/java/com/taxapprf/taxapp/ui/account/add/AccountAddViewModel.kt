package com.taxapprf.taxapp.ui.account.add

import android.text.Editable
import com.taxapprf.domain.user.SwitchAccountUseCase
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AccountAddViewModel @Inject constructor(
    private val switchAccountUseCase: SwitchAccountUseCase
) : BaseViewModel() {
    private var accountName = ""
    fun switchAccount() {
        if (isUnlock) {
            /*            val switchAccountModel = SwitchAccountModel(
                            account.id, account.accountKey,
                            activeAccountKey = accountName
                        )

                        viewModelScope.launch(Dispatchers.IO) {
                            flow<Unit> { switchAccountUseCase.execute(switchAccountModel) }
                                .onStart { startWithLock() }
                                .catch { error(it) }
                                .collectLatest { success() }
                        }*/
        }
    }

    fun checkName(cAccountName: Editable?): Int? {
        accountName = cAccountName.toString()
        return if (accountName.isErrorNameRange()) R.string.error_input_account_incorrect
        else null
    }

    private fun String.isErrorNameRange() = length < 3 || length > 16
}