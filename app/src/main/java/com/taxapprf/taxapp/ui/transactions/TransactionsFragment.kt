package com.taxapprf.taxapp.ui.transactions

import android.os.Bundle
import android.view.MenuItem
import android.view.View
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
import com.taxapprf.taxapp.ui.share
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TransactionsFragment : BaseFragment(R.layout.fragment_transactions) {
    private val binding by viewBinding(FragmentTransactionsBinding::bind)
    private val viewModel by viewModels<TransactionsViewModel>()
    private val adapter = TransactionsAdapter { transactionAdapterCallback }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepToolbar()
        prepView()
        prepListeners()

        viewModel.attachWithAccount()
        currentStackSavedState.observeDelete()
        viewModel.observeTransactions()
    }

    private fun prepToolbar() {
        toolbar.updateMenu(R.menu.transactions_toolbar) {
            when (it.itemId) {
                R.id.toolbar_share_excel -> {
                    if (requireActivity().checkStoragePermission()) {
                        viewModel.getExcelReportUri()
                    }

                    true
                }

                R.id.toolbar_export_excel -> {

                    true
                }

                R.id.toolbar_import_excel -> {
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
        getLiveData<Boolean>(DELETE_ACCEPTED).observe(viewLifecycleOwner) {
            if (it) {
                viewModel.deleteTransaction()
            } else viewModel.deleteTransaction = null
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

    /*    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {

            val textYearTax = binding!!.textTransYearSum
            viewModel.getSumTaxes().observe(viewLifecycleOwner, object : Observer<Double?> {
                override fun onChanged(yearTax: Double) {
                }
            })
            val buttonDownload: ImageButton = binding!!.buttonTransDownload
            buttonDownload.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View) {
                    if (isStoragePermissionGranted) {
                        fileName = viewModel.downloadStatement()
                        if (fileName!!.exists()) {
                            Snackbar.make(v, "Отчет скачан.", Snackbar.LENGTH_SHORT).show()
                            val uri = FileProvider.getUriForFile(
                                context!!, context!!.applicationContext.packageName + ".provider",
                                fileName!!
                            )
                            Log.d("OLGA", "onClick download uri: " + uri.path)
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            startActivity(intent)
                        } else {
                            Snackbar.make(v, "Не удалось скачать отчет.", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
            })
*/

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
        viewModel.emailUri?.let { requireActivity().share(it) }
    }

    private fun navToTransactionDelete() {
        findNavController().navigate(R.id.action_transactions_to_transactions_delete_dialog)
    }

    private fun navToTransactionDetail(transactionModel: TransactionModel) {
        mainViewModel.report = viewModel.report
        mainViewModel.transaction = transactionModel
        findNavController().navigate(R.id.action_transactions_to_transaction_detail)
    }

    companion object {
        const val DELETE_ACCEPTED = "delete_accepted"
    }
}