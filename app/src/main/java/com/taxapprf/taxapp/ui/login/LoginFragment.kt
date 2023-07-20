package com.taxapprf.taxapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.activities.LoginActivity
import com.taxapprf.taxapp.activities.MainActivity
import com.taxapprf.taxapp.databinding.FragmentLoginBinding


class LoginFragment : Fragment(R.layout.fragment_login) {
    private val binding by viewBinding(FragmentLoginBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonLoginSignIn.setOnClickListener { navToSignIn() }
        binding.buttonLoginRegister.setOnClickListener { navToRegister() }

        (requireActivity() as LoginActivity).viewModel.account.observe(viewLifecycleOwner) {
            it?.let {
                if (it.active) navToMainActivity()
                else navToSelectAccount()
            }
        }
    }

    private fun navToSignIn() {
        findNavController().navigate(R.id.action_loginFragment_to_signInFragment)
    }

    private fun navToRegister() {
        findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
    }

    private fun navToSelectAccount() {
        findNavController().navigate(R.id.action_loginFragment_to_selectAccountFragment)
    }

    private fun navToMainActivity() {
        startActivity(Intent(activity, MainActivity::class.java))
        requireActivity().finish()
    }
}