package com.taxapprf.taxapp.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taxapprf.domain.sync.SyncAllUseCase
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.transaction.SaveTransactionUseCase
import com.taxapprf.domain.user.ObserveUserUseCase
import com.taxapprf.domain.user.SignOutUseCase
import com.taxapprf.domain.user.SwitchAccountModel
import com.taxapprf.domain.user.SwitchAccountUseCase
import com.taxapprf.taxapp.ui.makeHot
import com.taxapprf.taxapp.ui.showLoading
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    observeUserUseCase: ObserveUserUseCase,
    private val switchAccountUseCase: SwitchAccountUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val saveTransactionUseCase: SaveTransactionUseCase,
    syncAllUseCase: SyncAllUseCase
) : ViewModel() {

    /*
    TODO() сделать загрузку в настройках курса за выбранный период
     viewModelScope.launch(Dispatchers.IO) {
                val date = LocalDate.now().toEpochDay()
                for (i in 0..900) {
                    getCBRRateUseCase.execute("USD", date - i)
                }
            }
            */

    val sync = syncAllUseCase.execute()

    val userWithAccounts =
        observeUserUseCase.execute()
            .showLoading()
            .makeHot(viewModelScope)

    fun switchAccount(accountName: String) =
        userWithAccounts.value?.user?.id?.let { userId ->
            viewModelScope.launch(Dispatchers.IO) {
                val switchAccountModel =
                    SwitchAccountModel(
                        userId = userId,
                        accountName = accountName
                    )
                switchAccountUseCase.execute(switchAccountModel)
            }
        }

    fun signOut() =
        viewModelScope.launch(Dispatchers.IO) {
            signOutUseCase.execute()
        }

    fun saveTransaction(saveTransactionModel: SaveTransactionModel) =
        viewModelScope.launch(Dispatchers.IO) {
            saveTransactionUseCase.execute(saveTransactionModel)
        }
}