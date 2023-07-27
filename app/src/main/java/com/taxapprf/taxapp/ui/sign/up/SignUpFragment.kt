package com.taxapprf.taxapp.ui.sign.up

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.databinding.FragmentSignUpBinding
import com.taxapprf.taxapp.ui.BaseFragment
import com.taxapprf.taxapp.ui.BaseState
import com.taxapprf.taxapp.ui.showSnackBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : BaseFragment(R.layout.fragment_sign_up) {
    private val binding by viewBinding(FragmentSignUpBinding::bind)
    private val viewModel by viewModels<SignUpViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonRegisterCancel.setOnClickListener { popBackStack() }
        binding.buttonRegisterCreate.setOnClickListener {
            val inputEmail = binding.editRegisterEmail.text.toString()
            val inputName = binding.editRegisterName.text.toString()
            val inputPhone = binding.editRegisterPhone.text.toString()
            val inputPassword = binding.editRegisterPassword.text.toString()
            viewModel.signUp(inputName, inputEmail, inputPassword, inputPhone)
        }

        viewModel.attachToBaseFragment()
        viewModel.state.observe(viewLifecycleOwner) {
            if (it is BaseState.Success) {
                binding.root.showSnackBar(R.string.messageRegistrationSuccess)
                navToAccountFirst()
            }
        }
    }

    override fun onLoadingError(stringResId: Int) {
        binding.root.showSnackBar(stringResId)
    }

    private fun navToAccountFirst() {
        findNavController().navigate(R.id.action_sign_up_to_account_first)
    }
}