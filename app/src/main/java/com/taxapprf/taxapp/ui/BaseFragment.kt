package com.taxapprf.taxapp.ui

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.taxapprf.data.error.SignUpErrorEmailAlreadyUse
import com.taxapprf.data.error.SignInErrorWrongPassword
import com.taxapprf.data.error.InputErrorEmailEmpty
import com.taxapprf.data.error.InputErrorEmailIncorrect
import com.taxapprf.data.error.InputErrorNameEmpty
import com.taxapprf.data.error.InputErrorPasswordLength
import com.taxapprf.data.error.InputErrorPhoneEmpty
import com.taxapprf.data.error.UserErrorSessionExpire
import com.taxapprf.taxapp.R

open class BaseFragment(layoutId: Int) : Fragment(layoutId) {
    private val loading by lazy { requireActivity() as Loading }
    private lateinit var baseViewModel: BaseViewModel

    protected fun BaseViewModel.attachToBaseFragment() {
        baseViewModel = this
        baseViewModel.state.observe(viewLifecycleOwner) {
            when (it) {
                is BaseState.Loading -> loading.onLoadingStart()
                is BaseState.Error -> prepOnLoadingError(it.t)
                is BaseState.Success -> loading.onLoadingSuccess()
                else -> {}
            }
        }
    }

    private fun prepOnLoadingError(t: Throwable) {
        loading.onLoadingStop()

        val stringResId = when (t) {
            is UserErrorSessionExpire -> R.string.auth_error_session_expire
            is InputErrorNameEmpty -> R.string.error_name_empty
            is InputErrorPhoneEmpty -> R.string.error_phone_empty
            is InputErrorEmailEmpty -> R.string.error_email_empty
            is InputErrorEmailIncorrect -> R.string.error_email_incorrect
            is InputErrorPasswordLength -> R.string.error_password_length
            is SignInErrorWrongPassword -> R.string.error_sign_in
            is SignUpErrorEmailAlreadyUse -> R.string.sign_up_error_email_already_use
            else -> throw t
        }

        onLoadingError(stringResId)
    }

    protected open fun onLoadingError(stringResId: Int) {
        loading.onLoadingError(stringResId)
    }

    protected fun popBackStack() {
        findNavController().popBackStack()
    }
}