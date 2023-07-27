package com.taxapprf.taxapp.ui.sign.`in`

import androidx.lifecycle.viewModelScope
import com.taxapprf.domain.user.SignInUseCase
import com.taxapprf.taxapp.ui.BaseViewModel
import com.taxapprf.data.error.InputErrorEmailEmpty
import com.taxapprf.data.error.InputErrorEmailIncorrect
import com.taxapprf.data.error.InputErrorPasswordLength
import com.taxapprf.domain.user.SignInModel
import com.taxapprf.taxapp.ui.isEmailPattern
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val singInUseCase: SignInUseCase,
) : BaseViewModel() {
    fun signIn(email: String, password: String) {
        if (email.isErrorInputEmailChecker()) return
        if (password.isErrorInputPasswordChecker()) return

        val signInModel = SignInModel(email, password)

        viewModelScope.launch(Dispatchers.IO) {
            singInUseCase.execute(signInModel)
                .onStart { loading() }
                .catch { error(it) }
                .collectLatest { success() }
        }
    }

    private fun String.isErrorInputEmailChecker(): Boolean {
        if (isEmpty()) error(InputErrorEmailEmpty())
        else if (!isEmailPattern()) error(InputErrorEmailIncorrect())
        else return false

        return true
    }

    private fun String.isErrorInputPasswordChecker(): Boolean {
        if (length < 8) error(InputErrorPasswordLength())
        else return false

        return true
    }
}