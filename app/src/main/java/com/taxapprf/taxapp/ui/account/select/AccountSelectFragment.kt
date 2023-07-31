package com.taxapprf.taxapp.ui.account.select

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.databinding.FragmentAccountSelectBinding
import com.taxapprf.taxapp.ui.BaseFragment
import com.taxapprf.taxapp.ui.BaseState
import com.taxapprf.taxapp.ui.LoginActivity
import com.taxapprf.taxapp.ui.MainActivity
import com.taxapprf.taxapp.ui.MainViewModel
import com.taxapprf.taxapp.ui.showSnackBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountSelectFragment : BaseFragment(R.layout.fragment_account_select) {
    private val binding by viewBinding(FragmentAccountSelectBinding::bind)
    private val viewModel by viewModels<AccountSelectViewModel>()
    private val activityViewModel by activityViewModels<MainViewModel>()
    private val adapter by lazy {
        ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.prepSelectSpinner()
        binding.buttonSelectNewAccountCreate.setOnClickListener { navToNewAccount() }
        binding.buttonSelectExit.setOnClickListener { viewModel.logOut() }

        viewModel.attachToBaseFragment()
        viewModel.observeState()
        activityViewModel.observeAccounts()
    }

    private fun FragmentAccountSelectBinding.prepSelectSpinner() {
        buttonSelectOpen.setOnClickListener {
            if (spinnerSelectAccount.selectedItem == null)
                it.showSnackBar(R.string.account_select_accounts_loading)
            // TODO() без проверки на наличие данных в спинере по нажатии открыть ошибка
            // TODO проверить смежные фрагменты
            val item = spinnerSelectAccount.selectedItem.toString()
            viewModel.saveAccount(item)
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSelectAccount.adapter = adapter
    }

    private fun AccountSelectViewModel.observeState() =
        state.observe(viewLifecycleOwner) {
            when (it) {
                is BaseState.LogOut -> navToLoginActivity()
                is BaseState.AccountSelect -> navToMainActivity()
                else -> {}
            }
        }

    private fun MainViewModel.observeAccounts() =
        user.observe(viewLifecycleOwner) { u ->
            u?.let { user ->
                user.accounts.map { it.name }
                adapter.clear()
                adapter.addAll()
            }
        }

    private fun navToLoginActivity() {
        startActivity(Intent(activity, LoginActivity::class.java))
        requireActivity().finish()
    }

    private fun navToMainActivity() {
        startActivity(Intent(activity, MainActivity::class.java))
        requireActivity().finish()
    }

    private fun navToNewAccount() {
        findNavController().navigate(R.id.action_account_select_to_account_new)
    }
}