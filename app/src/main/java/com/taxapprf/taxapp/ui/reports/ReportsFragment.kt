package com.taxapprf.taxapp.ui.reports

import android.content.Intent
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
import com.taxapprf.domain.report.ReportModel
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.databinding.FragmentReportsBinding
import com.taxapprf.taxapp.ui.BaseActionModeCallback
import com.taxapprf.taxapp.ui.BaseFragment
import com.taxapprf.taxapp.ui.checkStoragePermission
import com.taxapprf.taxapp.ui.dialogs.DeleteDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReportsFragment : BaseFragment(R.layout.fragment_reports) {
    private val binding by viewBinding(FragmentReportsBinding::bind)
    private val viewModel by viewModels<ReportsViewModel>()

    private val reportsAdapterCallback =
        object : ReportsAdapterCallback {
            override fun onItemClick(reportModel: ReportModel) {
                navToTransactions(reportModel)
            }

            override fun onMoreClick(reportModel: ReportModel) {
                onShowMoreClick(reportModel)
            }

            override fun onSwiped(reportModel: ReportModel) {
                navToDeleteDialog(reportModel)
            }
        }

    private val adapter = ReportsAdapter(reportsAdapterCallback)
    private val reportTouchHelper = ReportAdapterTouchHelperCallback(reportsAdapterCallback)
    private val itemTouchHelper = ItemTouchHelper(reportTouchHelper)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.attachWithAccount()
        currentStackSavedState.observeDelete()

        prepToolbar()
        prepViews()
        setListeners()
    }

    override fun onAuthReady() {
        super.onAuthReady()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.observeReports().collectLatest { reports ->
                    adapter.submitList(reports)
                }
            }
        }
    }

    private fun prepToolbar() {
        toolbar.updateToolbar(getString(R.string.taxes_name))
        toolbar.updateMenu(R.menu.reports_toolbar) {
            when (it.itemId) {
                R.id.toolbar_import_excel -> {
                    launchExportExcelToFirebaseIntent()
                    true
                }

                else -> false
            }
        }
    }

    private fun prepViews() {
        binding.recyclerYearStatements.adapter = adapter
        itemTouchHelper.attachToRecyclerView(binding.recyclerYearStatements)
    }

    private fun setListeners() {
        fab.setOnClickListener { navToTransactionDetail() }
    }

    private fun SavedStateHandle.observeDelete() {
        getLiveData<Int?>(DeleteDialogFragment.DELETE_ID).observe(viewLifecycleOwner) { result ->
            result?.let { viewModel.deleteReport(it) }
                ?: run {
                    reportTouchHelper.cancelSwipe()
                    itemTouchHelper.attachToRecyclerView(null)
                    itemTouchHelper.attachToRecyclerView(binding.recyclerYearStatements)
                }
        }
    }

    private fun onShowMoreClick(reportModel: ReportModel) {
        showActionMode {
            object : BaseActionModeCallback {
                override var menuInflater = requireActivity().menuInflater
                override var menuId = R.menu.report_action_menu

                override fun onActionItemClicked(
                    mode: ActionMode?,
                    item: MenuItem
                ) = when (item.itemId) {
                    R.id.action_menu_delete -> {
                        navToDeleteDialog(reportModel)
                        true
                    }

                    else -> false
                }
            }
        }
    }

    private val exportExcelToFirebaseIntent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            viewModel.saveReportsFromExcel(it.data)
        }

    private fun launchExportExcelToFirebaseIntent() {
        with(requireActivity()) {
            if (checkStoragePermission()) {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "application/vnd.ms-excel"
                exportExcelToFirebaseIntent.launch(intent)
            }
        }
    }

    private fun navToTransactions(reportModel: ReportModel) {
        findNavController().navigate(
            ReportsFragmentDirections.actionReportsToTransactions(reportModel.id)
        )
    }

    private fun navToTransactionDetail() {
        findNavController().navigate(ReportsFragmentDirections.actionReportsToTransactionDetail())
    }

    private fun navToDeleteDialog(reportModel: ReportModel) {
        actionMode?.finish()
        findNavController().navigate(
            ReportsFragmentDirections.actionReportsToDeleteDialog(reportModel.id)
        )
    }
}