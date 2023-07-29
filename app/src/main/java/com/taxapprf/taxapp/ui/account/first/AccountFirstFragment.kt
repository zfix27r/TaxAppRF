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

        viewModel.attachToBaseFragment()

        binding.buttonFirstAccountCreate.setOnClickListener {
            val account = binding.editFirstAccountName.text.toString()
            viewModel.save(account)
        }
        binding.buttonFirstAccountCancel.setOnClickListener { viewModel.save() }
        viewModel.state.observe(viewLifecycleOwner) {
            if (it is BaseState.Success) navToMainActivity()
        }
    }

    private fun navToMainActivity() {
        startActivity(Intent(activity, MainActivity::class.java))
        requireActivity().finish()
    }
}