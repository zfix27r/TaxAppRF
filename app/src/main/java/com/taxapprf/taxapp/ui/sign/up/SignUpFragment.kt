package com.taxapprf.taxapp.ui.sign.up

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.taxapprf.taxapp.app.R
import com.taxapprf.taxapp.app.databinding.FragmentSignUpBinding
import com.taxapprf.taxapp.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : BaseFragment(R.layout.fragment_sign_up) {
    private val binding by viewBinding(FragmentSignUpBinding::bind)
    private val viewModel by viewModels<SignUpViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.attach()

        toolbar.updateTitles()
        toolbar.updateMenu()
        updateUI()

        binding.buttonSignUpCreate.setOnClickListener { checkSignUp() }
    }

    override fun onSuccess() {
        super.onSuccess()
        mainViewModel.updateUserWithAccounts()
        navToReports()
    }

    private fun updateUI() {
        binding.editSignUpEmail.setText(viewModel.email)
        binding.editSignUpName.setText(viewModel.name)
        binding.editSignUpPhone.setText(viewModel.phone)
        binding.editSignUpPassword.setText(viewModel.password)
    }

    private fun checkSignUp() {
        viewModel.email = binding.editSignUpEmail.text.toString()
        viewModel.password = binding.editSignUpPassword.text.toString()

        viewModel.name = binding.editSignUpName.text.toString()
        viewModel.phone = binding.editSignUpPhone.text.toString()

        val updateEmailResult = viewModel.checkEmail()
            .updateEditError(binding.editSignUpEmail)
        val updatePasswordResult = viewModel.checkPassword()
            .updateEditError(binding.editSignUpPassword)

        val updateNameResult = viewModel.checkName()
            .updateEditError(binding.editSignUpName)
        val updatePhoneResult = viewModel.checkPhone()
            .updateEditError(binding.editSignUpPhone)

        if (updateEmailResult && updateNameResult && updatePhoneResult && updatePasswordResult)
            viewModel.signUp()
    }

    private fun navToReports() {
        findNavController().navigate(R.id.action_global_reports)
    }
}