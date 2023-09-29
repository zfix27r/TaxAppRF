package com.taxapprf.taxapp.ui.sign.up

import android.text.Editable
import androidx.lifecycle.viewModelScope
import com.taxapprf.domain.user.SignUpModel
import com.taxapprf.domain.user.SignUpUseCase
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
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
) : BaseViewModel() {
    private var email = ""
    private var name = ""
    private var phone = ""
    private var password = ""

    fun signUp() {
        if (isUnlock) {
            val signUpModel = SignUpModel(name, email, password, phone)

            viewModelScope.launch(Dispatchers.IO) {
/*                signUpUseCase.execute(signUpModel)
                    .onStart { start() }
                    .catch { error(it) }
                    .collectLatest { success() }*/
            }
        }
    }
    fun checkEmail(cEmail: Editable?): Int? {
        email = cEmail.toString()
        return if (email.isEmpty()) R.string.error_email_empty
        else if (email.isEmailIncorrect()) R.string.error_email_incorrect
        else null
    }

    fun checkName(cName: Editable?): Int? {
        name = cName.toString()
        return if (name.isErrorNameRange()) R.string.error_name_range
        else null
    }

    fun checkPhone(cPhone: Editable?): Int? {
        phone = cPhone.toString()
        return if (phone.isEmpty()) R.string.error_phone_empty
        else null
    }

    fun checkPassword(cPassword: Editable?): Int? {
        password = cPassword.toString()
        return if (password.isErrorPasswordRange()) R.string.error_password_length
        else null
    }

    private fun String.isErrorNameRange() = length < 3 || length > 16
}