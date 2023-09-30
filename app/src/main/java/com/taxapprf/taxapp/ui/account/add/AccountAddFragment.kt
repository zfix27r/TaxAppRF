package com.taxapprf.taxapp.ui.account.add

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.databinding.FragmentAccountAddBinding
import com.taxapprf.taxapp.ui.BaseBottomSheetFragment
import com.taxapprf.taxapp.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountAddFragment : BaseBottomSheetFragment(R.layout.fragment_account_add) {
    private val binding by viewBinding(FragmentAccountAddBinding::bind)
    private val viewModel by viewModels<AccountAddViewModel>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        return dialog.fullHeight()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.attachWithAccount()

//        toolbar.updateMenu()
    }

    override fun onAuthReady() {
        super.onAuthReady()
        binding.buttonAccountAddCreate.setOnClickListener { accountCreate() }
    }

    override fun onSuccess() {
        super.onSuccess()
        findNavController().popBackStack()
    }

    private fun accountCreate() {
        val updateNameResult = viewModel
            .checkName(binding.editAddAccountName.text)
            .updateEditError(binding.editAddAccountName)
        if (updateNameResult) viewModel.switchAccount()
    }
}