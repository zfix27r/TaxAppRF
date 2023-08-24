package com.taxapprf.taxapp.ui.sign.up

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.databinding.FragmentSignUpBinding
import com.taxapprf.taxapp.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : BaseFragment(R.layout.fragment_sign_up) {
    private val binding by viewBinding(FragmentSignUpBinding::bind)
    private val viewModel by viewModels<SignUpViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSignUpCreate.setOnClickListener { checkSignUp() }
        viewModel.attach()
    }

    override fun onSuccess() {
        super.onSuccess()

        mainViewModel.loading()
        drawer.showAuth()
        navToReports()
    }

    private fun checkSignUp() {
        val updateEmailResult = viewModel
            .checkEmail(binding.editSignUpEmail.text)
            .updateEditError(binding.editSignUpEmail)
        val updateNameResult = viewModel
            .checkName(binding.editSignUpName.text)
            .updateEditError(binding.editSignUpName)
        val updatePhoneResult = viewModel
            .checkPhone(binding.editSignUpPhone.text)
            .updateEditError(binding.editSignUpPhone)
        val updatePasswordResult = viewModel
            .checkPassword(binding.editSignUpPassword.text)
            .updateEditError(binding.editSignUpPassword)

        if (updateEmailResult && updateNameResult && updatePhoneResult && updatePasswordResult)
            viewModel.signUp()
    }

    private fun navToReports() {
        findNavController().navigate(R.id.action_global_reports)
    }
}