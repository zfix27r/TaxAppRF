package com.taxapprf.taxapp.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taxapprf.domain.main.SaveTransaction1Model
import com.taxapprf.domain.main.SaveTransaction1UseCase
import com.taxapprf.domain.sync.SyncAllUseCase
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.user.ObserveUserWithAccountsModel
import com.taxapprf.domain.user.ObserveUserWithAccountsUseCase
import com.taxapprf.domain.user.SignOutUseCase
import com.taxapprf.domain.user.SwitchAccountModel
import com.taxapprf.domain.user.SwitchAccountUseCase
import com.taxapprf.domain.user.UserWithAccountsModel
import com.taxapprf.taxapp.ui.showLoading
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val observeUserWithAccountsUseCase: ObserveUserWithAccountsUseCase,
    private val switchAccountUseCase: SwitchAccountUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val syncAllUseCase: SyncAllUseCase,
    private val saveTransaction1UseCase: SaveTransaction1UseCase,
) : ViewModel() {
    var defaultAccountName: String? = null

    /*
    TODO() сделать загрузку в настройках курса за выбранный период
     viewModelScope.launch(Dispatchers.IO) {
                val date = LocalDate.now().toEpochDay()
                for (i in 0..900) {
                    getCBRRateUseCase.execute("USD", date - i)
                }
            }
            */

    private val _userWithAccounts: MutableStateFlow<UserWithAccountsModel?> = MutableStateFlow(null)
    val userWithAccounts = _userWithAccounts.asStateFlow()

    var accountId: Int? = null

    suspend fun syncAll() =
        flow {
            syncAllUseCase.execute()
            emit(Unit)
        }
            .flowOn(Dispatchers.IO)
            .showLoading()

    fun updateUserWithAccounts() =
        defaultAccountName?.let { defaultAccountName ->
            viewModelScope.launch(Dispatchers.IO) {
                val observeUserWithAccountsModel =
                    ObserveUserWithAccountsModel(defaultAccountName)

                observeUserWithAccountsUseCase.execute(observeUserWithAccountsModel)
                    .collectLatest {
                        _userWithAccounts.value = it
                    }
            }
        }

    fun switchAccount(accountName: String) =
        userWithAccounts.value.let {
            it?.user?.id?.let { userId ->
                viewModelScope.launch(Dispatchers.IO) {
                    val switchAccountModel =
                        SwitchAccountModel(
                            userId = userId,
                            accountName = accountName
                        )
                    switchAccountUseCase.execute(switchAccountModel)
                }
            }
        }

    fun signOut() =
        viewModelScope.launch(Dispatchers.IO) {
            signOutUseCase.execute()
                .showLoading()
                .collectLatest {
                    updateUserWithAccounts()
                }
        }

    fun saveTransaction(saveTransactionModel: SaveTransactionModel) =
        viewModelScope.launch(Dispatchers.IO) {
            saveTransaction1UseCase.execute(
                SaveTransaction1Model(
                    transactionId = saveTransactionModel.transactionId,
                    reportId = saveTransactionModel.reportId,
                    accountId = saveTransactionModel.accountId,
                    transactionTypeOrdinal = saveTransactionModel.type.ordinal,
                    currencyOrdinal = saveTransactionModel.currencyOrdinal,
                    name = saveTransactionModel.name,
                    date = saveTransactionModel.date,
                    sum = saveTransactionModel.sum,
                    tax = saveTransactionModel.tax
                )
            )
//            saveTransactionUseCase.execute(saveTransactionModel)
        }
}