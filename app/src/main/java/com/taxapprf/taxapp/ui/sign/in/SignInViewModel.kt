package com.taxapprf.taxapp.ui.sign.`in`

import android.text.Editable
import androidx.lifecycle.viewModelScope
import com.taxapprf.domain.user.SignInModel
import com.taxapprf.domain.user.SignInUseCase
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.ui.BaseViewModel
import com.taxapprf.taxapp.ui.isEmailIncorrect
import com.taxapprf.taxapp.ui.isErrorPasswordRange
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
    private var email = ""
    private var password = ""

    fun signIn() {
        if (isUnlock) {
            val signInModel = SignInModel(email, password)

            viewModelScope.launch(Dispatchers.IO) {
/*                singInUseCase.execute(signInModel)
                    .onStart { start() }
                    .catch { error(it) }
                    .collectLatest { success() }*/
            }
        }
    }

    fun checkEmail(cEmail: Editable?) = check {
        email = cEmail.toString()
        if (email.isEmpty()) R.string.error_email_empty
        else if (email.isEmailIncorrect()) R.string.error_email_incorrect
        else null
    }

    fun checkPassword(cPassword: Editable?) = check {
        password = cPassword.toString()
        if (password.isErrorPasswordRange()) R.string.error_password_length
        else null
    }
}