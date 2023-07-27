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
import com.taxapprf.taxapp.ui.MainActivity
import com.taxapprf.taxapp.ui.showSnackBar
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
        binding.buttonChangeAccountCreate.setOnClickListener {
            val account = binding.editChangeAccountName.text.toString()
            viewModel.save(account)
            navToMainActivity()
        }
        binding.buttonChangeAccountCreate.setOnClickListener { popBackStack() }
        binding.buttonChangeAccountOpen.setOnClickListener {
            binding.spinnerChangeAccount.selectedItem?.let {
                viewModel.save(it as String)
                navToMainActivity()
            } ?: run {
                binding.root.showSnackBar(R.string.account_change_message_waite_loading)
            }
        }

        viewModel.attachToBaseFragment()
        viewModel.accounts.observe(viewLifecycleOwner) {
            adapter.clear()
            adapter.addAll(it)
        }
    }

    private fun prepSpinner() {
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerChangeAccount.adapter = adapter
    }

    private fun navToMainActivity() {
        startActivity(Intent(activity, MainActivity::class.java))
        requireActivity().finish()
    }
}