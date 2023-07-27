package com.taxapprf.taxapp.ui.account.select

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.databinding.FragmentAccountSelectBinding
import com.taxapprf.taxapp.ui.BaseFragment
import com.taxapprf.taxapp.ui.BaseState
import com.taxapprf.taxapp.ui.MainActivity
import com.taxapprf.taxapp.ui.showSnackBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountSelectFragment : BaseFragment(R.layout.fragment_account_select) {
    private val binding by viewBinding(FragmentAccountSelectBinding::bind)
    private val viewModel by viewModels<AccountSelectViewModel>()
    private val adapter by lazy {
        ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.prepSpinner()

        binding.buttonSelectNewAccountCreate.setOnClickListener { navToNewAccount() }
        binding.buttonSelectExit.setOnClickListener { viewModel.logOut() }

        viewModel.attachToBaseFragment()
        viewModel.state.observe(viewLifecycleOwner) { it.execute() }
        viewModel.accounts.observe(viewLifecycleOwner) {
            adapter.clear()
            adapter.addAll(it)
        }
    }

    private fun BaseState.execute() =
        when (this) {
            is BaseState.LogOut -> popBackStack()
            is BaseState.AccountSelect -> navToMainActivity()
            else -> {}
        }

    private fun FragmentAccountSelectBinding.prepSpinner() {
        buttonSelectOpen.setOnClickListener {
            if (spinnerSelectAccount.selectedItem == null)
                it.showSnackBar(R.string.loading)

            val item = spinnerSelectAccount.selectedItem.toString()
            viewModel.setActiveAccount(item)
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSelectAccount.adapter = adapter
    }

    private fun navToMainActivity() {
        startActivity(Intent(activity, MainActivity::class.java))
        requireActivity().finish()
    }

    private fun navToNewAccount() {
        findNavController().navigate(R.id.action_account_select_to_account_new)
    }
}