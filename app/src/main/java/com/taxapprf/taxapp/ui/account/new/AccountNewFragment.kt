package com.taxapprf.taxapp.ui.account.new

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.databinding.FragmentAccountNewBinding
import com.taxapprf.taxapp.ui.BaseFragment
import com.taxapprf.taxapp.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountNewFragment : BaseFragment(R.layout.fragment_account_new) {
    private val binding by viewBinding(FragmentAccountNewBinding::bind)
    private val viewModel by viewModels<AccountNewViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.attachToBaseFragment()

        binding.buttonNewAccountCreate.setOnClickListener {
            val account = binding.editNewAccountName.text.toString()
            viewModel.save(account)
            navToMainActivity()
        }
        binding.buttonNewAccountCancel.setOnClickListener { popBackStack() }
    }

    private fun navToMainActivity() {
        startActivity(Intent(activity, MainActivity::class.java))
        requireActivity().finish()
    }
}