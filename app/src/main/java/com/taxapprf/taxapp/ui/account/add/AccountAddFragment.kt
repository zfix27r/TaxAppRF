package com.taxapprf.taxapp.ui.account.add

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.databinding.FragmentAccountAddBinding
import com.taxapprf.taxapp.ui.BaseBottomSheetFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountAddFragment : BaseBottomSheetFragment(R.layout.fragment_account_add) {
    private val binding by viewBinding(FragmentAccountAddBinding::bind)

    interface AccountAddDialogListener {
        fun onAccountAddPositiveClick(dialog: AccountAddFragment)
        fun onAccountAddNegativeClick(dialog: AccountAddFragment)
    }

    private lateinit var listener: AccountAddDialogListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as AccountAddDialogListener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        return dialog.wrapHeight()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.editAddAccountName.requestFocus()
        binding.buttonAccountAddSave.setOnClickListener { listener.onAccountAddPositiveClick(this) }
        binding.buttonAccountAddDismiss.setOnClickListener { listener.onAccountAddNegativeClick(this) }
    }

    fun getAccountName() = binding.editAddAccountName.text.toString()
}