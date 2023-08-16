package com.taxapprf.taxapp.ui.sign.up

import androidx.lifecycle.viewModelScope
import com.taxapprf.domain.user.SignUpModel
import com.taxapprf.domain.user.SignUpUseCase
import com.taxapprf.taxapp.ui.BaseViewModel
import com.taxapprf.taxapp.ui.error.UIErrorEmailEmpty
import com.taxapprf.taxapp.ui.error.UIErrorEmailIncorrect
import com.taxapprf.taxapp.ui.error.UIErrorNameEmpty
import com.taxapprf.taxapp.ui.error.UIErrorPasswordLength
import com.taxapprf.taxapp.ui.error.UIErrorPhoneEmpty
import com.taxapprf.taxapp.ui.isEmailPattern
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
) : BaseViewModel() {
    fun signUp(name: String, email: String, password: String, phone: String) {
        if (name.isErrorInputNameChecker()) return
        if (phone.isErrorInputPhoneChecker()) return
        if (email.isErrorInputEmailChecker()) return
        if (password.isErrorInputPasswordChecker()) return

        val signUpModel = SignUpModel(name, email, password, phone)

        viewModelScope.launch(Dispatchers.IO) {
            signUpUseCase.execute(signUpModel)
                .onStart { start() }
                .catch { error(it) }
                .collectLatest { success() }
        }
    }

    private fun String.isErrorInputNameChecker(): Boolean {
        if (isEmpty()) error(UIErrorNameEmpty())
        else return false

        return true
    }

    private fun String.isErrorInputEmailChecker(): Boolean {
        if (isEmpty()) error(UIErrorEmailEmpty())
        else if (!isEmailPattern()) error(UIErrorEmailIncorrect())
        else return false

        return true
    }

    private fun String.isErrorInputPasswordChecker(): Boolean {
        if (length < 8) error(UIErrorPasswordLength())
        else return false

        return true
    }

    private fun String.isErrorInputPhoneChecker(): Boolean {
        if (isEmpty()) error(UIErrorPhoneEmpty())
        else return false

        return true
    }
}