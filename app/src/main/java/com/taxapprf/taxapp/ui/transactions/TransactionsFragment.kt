package com.taxapprf.taxapp.ui.transactions

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.view.ActionMode
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.domain.transaction.TransactionType
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.databinding.FragmentTransactionsBinding
import com.taxapprf.taxapp.ui.BaseActionModeCallback
import com.taxapprf.taxapp.ui.BaseFragment
import com.taxapprf.taxapp.ui.checkStoragePermission
import com.taxapprf.taxapp.ui.round
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class TransactionsFragment : BaseFragment(R.layout.fragment_transactions) {
    private val binding by viewBinding(FragmentTransactionsBinding::bind)
    private val viewModel by viewModels<TransactionsViewModel>()

    private val transactionAdapterCallback =
        object : TransactionsAdapterCallback {
            override fun onItemClick(transactionModel: TransactionModel) {
                navToTransactionDetail(transactionModel)
            }

            override fun onMoreClick(transactionModel: TransactionModel) {
                onShowMoreClick(transactionModel)
            }
        }

    private val adapter = TransactionsAdapter(transactionAdapterCallback)

    private val transactionAdapterTouchHelperCallback =
        object : TransactionsAdapterTouchHelperCallback {
            override fun onSwiped(transactionModel: TransactionModel) {
                showDeleteDialog(transactionModel)
            }
        }

    private val transactionTouchHelper =
        TransactionsAdapterTouchHelper(transactionAdapterTouchHelperCallback)
    private val itemTouchHelper: ItemTouchHelper = ItemTouchHelper(transactionTouchHelper)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.attachWithAccount()

        prepToolbar()
        prepView()
        prepListeners()
    }

    private fun prepToolbar() {
        toolbar.updateMenu(R.menu.transactions_toolbar) { menuItem ->
            when (menuItem.itemId) {
                R.id.toolbar_share_excel -> {
                    shareExcel()
                    true
                }

                R.id.toolbar_export_excel -> {
                    exportExcel()
                    true
                }

                else -> false
            }
        }
    }

    private fun prepView() {
        binding.recyclerTransactions.adapter = adapter
        itemTouchHelper.attachToRecyclerView(binding.recyclerTransactions)
    }

    private fun prepListeners() {
        fab.setOnClickListener { navToTransactionDetail() }
    }

    override fun onAuthReady() {
        super.onAuthReady()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.observeReport()?.let { flow ->
                    flow.collectLatest { report ->
                        report?.updateToolbar()
                    }
                } ?: findNavController().popBackStack()
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.observeTransactions()?.let { flow ->
                    flow.collectLatest { transactions ->
                        transactions?.let {
                            if (transactions.isNotEmpty()) adapter.submitList(transactions)
                            else findNavController().popBackStack()
                        }
                    }
                }
            }
        }
    }

    override fun onSuccessImport(uri: Uri) {
        super.onSuccessImport(uri)
        launchImportExcelIntent(uri)
    }

    override fun onSuccessDelete() {
        super.onSuccessDelete()
        findNavController().popBackStack()
    }

    private fun ReportModel.updateToolbar() {
        val title = String.format(getString(R.string.transactions_title), name)
        val subtitle = String.format(getString(R.string.transactions_subtitle), tax.round())
        toolbar.updateToolbar(title, subtitle)
    }

    private val transactionTypes
        get() = mapOf(
            TransactionType.TRADE.k to getString(R.string.transaction_type_trade),
            TransactionType.FUNDING_WITHDRAWAL.k to getString(R.string.transaction_type_funding_withdrawal),
            TransactionType.COMMISSION.k to getString(R.string.transaction_type_commission),
        )

    private fun shareExcel() {
        if (requireActivity().checkStoragePermission()) {
            viewModel.shareReport(transactionTypes)
        }
    }

    private fun exportExcel() {
        if (requireActivity().checkStoragePermission()) {
            viewModel.exportReport(transactionTypes)
        }
    }

    private val importExcelIntent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

        }

    private fun launchImportExcelIntent(uri: Uri) {
        if (fragment.requireActivity().checkStoragePermission()) {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.action = Intent.ACTION_VIEW
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            intent.type = "application/vnd.ms-excel"
            intent.setDataAndType(uri, "application/vnd.ms-excel")
            importExcelIntent.launch(intent)
        }
    }

    private fun onShowMoreClick(transactionModel: TransactionModel) {
        showActionMode {
            object : BaseActionModeCallback {
                override var menuInflater = requireActivity().menuInflater
                override var menuId = R.menu.transaction_action_menu

                override fun onActionItemClicked(
                    mode: ActionMode?,
                    item: MenuItem
                ) = when (item.itemId) {
                    R.id.action_menu_delete -> {
                        showDeleteDialog(transactionModel)
                        true
                    }

                    else -> false
                }
            }
        }
    }

    private fun showDeleteDialog(transactionModel: TransactionModel) {
        actionMode?.finish()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_dialog_title)
            .setMessage(R.string.delete_dialog_message)
            .setPositiveButton(R.string.delete_dialog_ok) { _, _ ->
                viewModel.deleteTransaction(transactionModel)
            }
            .setNegativeButton(R.string.delete_dialog_cancel) { _, _ ->
                transactionTouchHelper.cancelSwipe()
                itemTouchHelper.attachToRecyclerView(null)
                itemTouchHelper.attachToRecyclerView(binding.recyclerTransactions)
            }
            .show()
    }

    private fun navToTransactionDetail(transactionModel: TransactionModel? = null) {
        viewModel.reportId?.let {
            findNavController().navigate(
                TransactionsFragmentDirections.actionTransactionsToTransactionDetail(
                    reportId = it,
                    transactionId = transactionModel?.id ?: 0
                )
            )
        }
    }
}