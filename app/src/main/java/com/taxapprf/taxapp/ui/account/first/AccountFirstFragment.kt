package com.taxapprf.taxapp.ui.account.first

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.databinding.FragmentAccountFirstBinding
import com.taxapprf.taxapp.ui.BaseFragment
import com.taxapprf.taxapp.ui.BaseState
import com.taxapprf.taxapp.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountFirstFragment : BaseFragment(R.layout.fragment_account_first) {
    private val binding by viewBinding(FragmentAccountFirstBinding::bind)
    private val viewModel by viewModels<AccountFirstViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirstAccountCreate.setOnClickListener { accountCreate() }
        binding.buttonFirstAccountCancel.setOnClickListener { accountCreate() }

        viewModel.attachToBaseFragment()
        viewModel.observeState()
    }

    private fun AccountFirstViewModel.observeState() =
        state.observe(viewLifecycleOwner) {
            when (it) {
                is BaseState.Success -> navToMainActivity()
                else -> {}
            }
        }

    private fun accountCreate() {
        val account = binding.editFirstAccountName.text.toString()
        viewModel.saveAccount(account, getString(R.string.default_account_name))
    }

    private fun navToMainActivity() {
        startActivity(Intent(activity, MainActivity::class.java))
        requireActivity().finish()
    }
}