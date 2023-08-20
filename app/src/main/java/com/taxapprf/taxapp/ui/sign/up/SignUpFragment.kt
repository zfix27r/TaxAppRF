package com.taxapprf.taxapp.ui.sign.up

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.databinding.FragmentSignUpBinding
import com.taxapprf.taxapp.ui.BaseFragment
import com.taxapprf.taxapp.ui.getErrorDescription
import com.taxapprf.taxapp.ui.showSnackBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : BaseFragment(R.layout.fragment_sign_up) {
    private val binding by viewBinding(FragmentSignUpBinding::bind)
    private val viewModel by viewModels<SignUpViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSignUpCreate.setOnClickListener { signUp() }

        viewModel.attach()
    }

    override fun onSuccess() {
        super.onSuccess()

        mainViewModel.loading()
        drawer.showAuth()
        navToReports()
    }

    override fun onError(t: Throwable) {
        super.onError(t)
        binding.root.showSnackBar(t.getErrorDescription())
    }

    private fun signUp() {
        val inputEmail = binding.editSignUpEmail.text.toString()
        val inputName = binding.editSignUpName.text.toString()
        val inputPhone = binding.editSignUpPhone.text.toString()
        val inputPassword = binding.editSignUpPassword.text.toString()
        viewModel.signUp(inputName, inputEmail, inputPassword, inputPhone)
    }

    private fun navToReports() {
        findNavController().navigate(R.id.action_global_reports)
    }
}