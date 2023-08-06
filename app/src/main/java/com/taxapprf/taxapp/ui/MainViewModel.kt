package com.taxapprf.taxapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.taxapprf.domain.user.GetUserUseCase
import com.taxapprf.domain.user.IsSignInUseCase
import com.taxapprf.domain.user.UserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val isSignInUseCase: IsSignInUseCase,
    private val getUserUseCase: GetUserUseCase,
) : ViewModel() {
    // TODO перенести сюда состояния, для подключения. Нет индикации загрузки при заходе уже авторизованным при медленном инете
    val isSignIn
        get() = isSignInUseCase.execute()

    private var _name: String? = null
    val name
        get() = _name
    private var _email: String = ""
    val email
        get() = _email
    private var _phone: String = ""
    val phone
        get() = _phone

    fun loadUser() = viewModelScope.launch(Dispatchers.IO) {
        getUserUseCase.execute()
            .collectLatest {

            }
    }


    var account: String = ""
    var year: String = ""
    var transactionKey: String? = null
}