package com.taxapprf.taxapp.ui.sign.up

import androidx.lifecycle.viewModelScope
import com.taxapprf.domain.user.SignUpModel
import com.taxapprf.domain.user.SignUpUseCase
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
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
) : BaseViewModel() {
    var email = ""
    var name = ""
    var phone = ""
    var password = ""

    fun signUp() {
        if (isUnlock) {
            val signUpModel = SignUpModel(name, email, password, phone)

            viewModelScope.launch(Dispatchers.IO) {
                signUpUseCase.execute(signUpModel)
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

    fun checkName(): Int? {
        return if (name.isErrorNameRange()) R.string.error_name_range
        else null
    }

    fun checkPhone(): Int? {
        return if (phone.isEmpty()) R.string.error_phone_empty
        else null
    }

    fun checkPassword(): Int? {
        return if (password.isErrorPasswordRange()) R.string.error_password_length
        else null
    }

    private fun String.isErrorNameRange() = length < 3 || length > 16
}