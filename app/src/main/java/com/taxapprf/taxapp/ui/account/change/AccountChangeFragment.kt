package com.taxapprf.taxapp.ui.account.change

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.taxapprf.domain.account.AccountModel
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.databinding.FragmentAccountChangeBinding
import com.taxapprf.taxapp.ui.BaseFragment
import com.taxapprf.taxapp.ui.BaseState
import com.taxapprf.taxapp.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountChangeFragment : BaseFragment(R.layout.fragment_account_change) {
    private val binding by viewBinding(FragmentAccountChangeBinding::bind)
    private val viewModel by viewModels<AccountChangeViewModel>()
    private val adapter by lazy {
        ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonChangeAccountCreate.setOnClickListener { accountCreate() }
        binding.buttonChangeAccountOpen.setOnClickListener { accountOpen() }

        viewModel.attachToBaseFragment()
        viewModel.observeState()

        activityViewModel.accounts.observe(viewLifecycleOwner) {
            viewModel.accounts = it
            prepSpinner(it)
        }
    }

    private fun prepSpinner(accounts: List<AccountModel>) {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerChangeAccount.adapter = adapter

        var activeAccountIndex = 0
        val accountsName = accounts.mapIndexed { index, accountModel ->
            if (accountModel.active) activeAccountIndex = index
            accountModel.name
        }

        adapter.clear()
        adapter.addAll(accountsName)
        binding.spinnerChangeAccount.setSelection(activeAccountIndex)
    }

    private fun AccountChangeViewModel.observeState() =
        state.observe(viewLifecycleOwner) {
            when (it) {
                is BaseState.SuccessEdit -> navToMainActivity()
                else -> {}
            }
        }

    private fun accountCreate() {
        val accountName = binding.editChangeAccountName.text.toString()
        viewModel.changeAccount(accountName)
    }

    private fun accountOpen() {
        binding.spinnerChangeAccount.selectedItem?.let {
            val accountName = it as String
            viewModel.changeAccount(accountName)
        }
    }

    private fun navToMainActivity() {
        startActivity(Intent(activity, MainActivity::class.java))
        requireActivity().finish()
    }
}