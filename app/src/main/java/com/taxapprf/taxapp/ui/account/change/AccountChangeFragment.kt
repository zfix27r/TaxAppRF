package com.taxapprf.taxapp.ui.account.change

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
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

        prepSpinner()
        binding.buttonChangeAccountCreate.setOnClickListener { accountCreate() }
        binding.buttonChangeAccountOpen.setOnClickListener { accountOpen() }

        viewModel.attachToBaseFragment()
        viewModel.observeState()
        viewModel.observeAccounts()
    }

    private fun prepSpinner() {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerChangeAccount.adapter = adapter
    }

    private fun AccountChangeViewModel.observeState() =
        state.observe(viewLifecycleOwner) {
            when (it) {
                is BaseState.SuccessEdit -> navToMainActivity()
                else -> {}
            }
        }

    private fun AccountChangeViewModel.observeAccounts() =
        accounts.observe(viewLifecycleOwner) { accounts ->
            adapter.clear()
            adapter.addAll(accounts)
            binding.spinnerChangeAccount.setSelection(viewModel.activeAccountPosition)
        }

    private fun accountCreate() {
        val accountName = binding.editChangeAccountName.text.toString()
        viewModel.saveAccount(accountName)
    }

    private fun accountOpen() {
        binding.spinnerChangeAccount.selectedItem?.let {
            val accountName = it as String
            viewModel.saveAccount(accountName)
        }
    }

    private fun navToMainActivity() {
        startActivity(Intent(activity, MainActivity::class.java))
        requireActivity().finish()
    }
}