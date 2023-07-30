package com.taxapprf.taxapp.ui.account.change

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.databinding.FragmentAccountChangeBinding
import com.taxapprf.taxapp.ui.BaseFragment
import com.taxapprf.taxapp.ui.BaseState
import com.taxapprf.taxapp.ui.MainActivity
import com.taxapprf.taxapp.ui.MainViewModel
import com.taxapprf.taxapp.ui.showSnackBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountChangeFragment : BaseFragment(R.layout.fragment_account_change) {
    private val binding by viewBinding(FragmentAccountChangeBinding::bind)
    private val viewModel by viewModels<AccountChangeViewModel>()
    private val activityViewModel by activityViewModels<MainViewModel>()
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
        activityViewModel.observeAccounts()
    }

    private fun prepSpinner() {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerChangeAccount.adapter = adapter
    }

    private fun AccountChangeViewModel.observeState() =
        state.observe(viewLifecycleOwner) {
            when (it) {
                is BaseState.Success -> TODO("какая должна быть реакция?")
                else -> {}
            }
        }

    private fun MainViewModel.observeAccounts() =
        accounts.observe(viewLifecycleOwner) { l ->
            adapter.clear()
            adapter.addAll(l.map { it.name })
        }

    private fun accountCreate() {
        val account = binding.editChangeAccountName.text.toString()
        viewModel.save(account)
        navToMainActivity()
    }

    private fun accountOpen() {
        binding.spinnerChangeAccount.selectedItem?.let {
            viewModel.save(it as String)
            navToMainActivity()
        } ?: run {
            binding.root.showSnackBar(R.string.account_change_message_waite_loading)
        }
    }

    private fun navToMainActivity() {
        startActivity(Intent(activity, MainActivity::class.java))
        requireActivity().finish()
    }
}