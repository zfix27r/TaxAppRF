package com.taxapprf.taxapp.ui.transactions

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.view.ActionMode
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.databinding.FragmentTransactionsBinding
import com.taxapprf.taxapp.ui.BaseActionModeCallback
import com.taxapprf.taxapp.ui.BaseFragment
import com.taxapprf.taxapp.ui.checkStoragePermission
import com.taxapprf.taxapp.ui.dialogs.DeleteDialogFragment
import com.taxapprf.taxapp.ui.share
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TransactionsFragment : BaseFragment(R.layout.fragment_transactions) {
    private val binding by viewBinding(FragmentTransactionsBinding::bind)
    private val viewModel by viewModels<TransactionsViewModel>()
    private val adapter = TransactionsAdapter { transactionAdapterCallback }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.attachWithAccount()
        currentStackSavedState.observeDelete()

        prepToolbar()
        prepView()
        prepListeners()

        viewModel.observeTransactions()
    }

    private fun prepToolbar() {
        toolbar.updateMenu(R.menu.transactions_toolbar) { menuItem ->
            when (menuItem.itemId) {
                R.id.toolbar_share_excel -> {
                    if (requireActivity().checkStoragePermission()) {
                        viewModel.getExcelToShare()
                    }

                    true
                }

                R.id.toolbar_export_excel -> {
                    if (requireActivity().checkStoragePermission()) {
                        viewModel.getExcelToStorage()
                    }

                    true
                }

                R.id.toolbar_import_excel -> {
                    launchImportExcelFromStorageIntent()
                    true
                }

                else -> false
            }
        }
    }

    private fun prepView() {
        binding.recyclerTransactions.adapter = adapter
    }

    private fun prepListeners() {
        fab.setOnClickListener {
            mainViewModel.report = viewModel.report
            findNavController().navigate(R.id.action_transactions_to_transaction_detail)
        }
    }

    private fun SavedStateHandle.observeDelete() {
        getLiveData<Boolean>(DeleteDialogFragment.DELETE_ACCEPTED).observe(viewLifecycleOwner) {
            if (it) viewModel.deleteTransaction()
            else viewModel.deleteTransaction = null
        }
    }

    override fun onAuthReady() {
        super.onAuthReady()

        mainViewModel.report?.let {
            viewModel.report = it
            mainViewModel.report = null
            viewModel.loadTransactions()
            updateUI()
        } ?: findNavController().popBackStack()
    }

    private fun TransactionsViewModel.observeTransactions() =
        transactions.observe(viewLifecycleOwner) { transaction ->
            transaction?.let {
                if (it.isEmpty()) findNavController().popBackStack()
                adapter.submitList(it)
                updateUI()
            } ?: findNavController().popBackStack()
        }

    override fun onSuccessShare() {
        super.onSuccessShare()
        startIntentSendEmail()
    }

    override fun onSuccessImport() {
        super.onSuccessImport()
        launchSaveExcelToStorageIntent(viewModel.excelUri)
    }

    override fun onSuccessDelete() {
        super.onSuccessDelete()
        findNavController().popBackStack()
    }

    private fun updateUI() {
        val title = String.format(getString(R.string.transactions_title), viewModel.report.year)
        val subtitle =
            String.format(getString(R.string.transactions_subtitle), viewModel.report.tax)
        toolbar.updateToolbar(title, subtitle)
    }

    private val transactionAdapterCallback = object : TransactionsAdapterCallback {
        override fun onClick(transactionModel: TransactionModel) {
            navToTransactionDetail(transactionModel)
        }

        override fun onClickMore(transactionModel: TransactionModel) {
            showActionMode {
                object : BaseActionModeCallback {
                    override var menuInflater = requireActivity().menuInflater
                    override var menuId = R.menu.transaction_action_menu

                    override fun onActionItemClicked(
                        mode: ActionMode?,
                        item: MenuItem
                    ) = when (item.itemId) {
                        R.id.action_menu_delete -> {
                            viewModel.deleteTransaction = transactionModel
                            actionMode?.finish()
                            navToTransactionDelete()
                            true
                        }

                        else -> false
                    }
                }
            }
        }
    }

    private fun startIntentSendEmail() {
        requireActivity().share(viewModel.excelUri)
    }

    private val getExcelFromStorageIntent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            viewModel.saveReportsFromExcel(it?.data?.data?.path)
        }

    private val importExcelToStorageIntent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

        }

    private fun launchImportExcelFromStorageIntent() {
        with(requireActivity()) {
            if (checkStoragePermission()) {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "application/vnd.ms-excel"
                getExcelFromStorageIntent.launch(intent)
            }
        }
    }

    private fun launchSaveExcelToStorageIntent(uri: Uri) {
        with(requireActivity()) {
            if (checkStoragePermission()) {
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                importExcelToStorageIntent.launch(intent)
            }
        }
    }

    private fun navToTransactionDelete() {
        findNavController().navigate(R.id.action_transactions_to_delete_dialog)
    }

    private fun navToTransactionDetail(transactionModel: TransactionModel) {
        mainViewModel.report = viewModel.report
        mainViewModel.transaction = transactionModel
        findNavController().navigate(R.id.action_transactions_to_transaction_detail)
    }
}