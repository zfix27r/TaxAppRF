package com.taxapprf.taxapp.ui.sign

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.databinding.FragmentSignBinding
import com.taxapprf.taxapp.ui.BaseFragment
import com.taxapprf.taxapp.ui.MainActivity
import com.taxapprf.taxapp.ui.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignFragment : BaseFragment(R.layout.fragment_sign) {
    private val binding by viewBinding(FragmentSignBinding::bind)
    private val viewModel by activityViewModels<MainViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonLoginSignIn.setOnClickListener { navToSignIn() }
        binding.buttonLoginRegister.setOnClickListener { navToSignUp() }
        viewModel.observeUser()
    }

    private fun MainViewModel.observeUser() {
        user.observe(viewLifecycleOwner) { user ->
            user?.let {
                if (user.accounts.isNotEmpty()) navToMainActivity()
                else navToAccountFirst()
            } ?: run {
                binding.signContent.isVisible = true
            }
        }
    }

    private fun navToAccountFirst() {
        findNavController().navigate(R.id.action_sign_to_account_first)
    }

    private fun navToMainActivity() {
        startActivity(Intent(activity, MainActivity::class.java))
        requireActivity().finish()
    }

    private fun navToSignIn() {
        findNavController().navigate(R.id.action_sign_to_sign_in)
    }

    private fun navToSignUp() {
        findNavController().navigate(R.id.action_sign_to_sign_up)
    }
}