package com.taxapprf.taxapp.ui.sign.`in`

import androidx.lifecycle.viewModelScope
import com.taxapprf.domain.user.SignInModel
import com.taxapprf.domain.user.SignInUseCase
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.ui.BaseViewModel
import com.taxapprf.taxapp.ui.isEmailIncorrect
import com.taxapprf.taxapp.ui.isErrorPasswordRange
import com.taxapprf.taxapp.ui.showLoading
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val singInUseCase: SignInUseCase,
) : BaseViewModel() {
    var email = ""
    var password = ""

    fun signIn() {
        if (isUnlock) {
            val signInModel = SignInModel(email, password)

            viewModelScope.launch(Dispatchers.IO) {
                singInUseCase.execute(signInModel)
                    .showLoading()
                    .collect()
            }
        }
    }

    fun checkEmail(): Int? {
        return if (email.isEmpty()) R.string.error_email_empty
        else if (email.isEmailIncorrect()) R.string.error_email_incorrect
        else null
    }

    fun checkPassword(): Int? {
        return if (password.isErrorPasswordRange()) R.string.error_password_length
        else null
    }
}