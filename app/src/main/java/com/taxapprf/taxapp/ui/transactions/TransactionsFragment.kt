package com.taxapprf.taxapp.ui.transactions

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.databinding.FragmentTransactionsBinding
import com.taxapprf.taxapp.ui.BaseFragment
import com.taxapprf.taxapp.ui.BaseState
import com.taxapprf.taxapp.ui.checkStoragePermission
import com.taxapprf.taxapp.ui.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class TransactionsFragment : BaseFragment(R.layout.fragment_transactions) {
    private val binding by viewBinding(FragmentTransactionsBinding::bind)
    private val viewModel by viewModels<TransactionsViewModel>()
    private val adapter = TransactionsAdapter {
        object : TransactionsAdapterCallback {
            override fun onClick(transactionModel: TransactionModel) {
                navToTransactionDetail(transactionModel)
            }
        }
    }

    private var fileName: File? = null

    //private val createExcelInLocal: CreateExcelInLocal? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activityViewModel.account.observe(viewLifecycleOwner) { account ->
            viewModel.account = account
        }

        viewModel.year = activityViewModel.report!!.year
        viewModel.loadTransactions()

        binding.recyclerTransactions.adapter = adapter

        viewModel.transactions.observe(viewLifecycleOwner) { transaction ->
            transaction?.let {
                adapter.submitList(it)

                /*                binding.textTransYearSum.text = String.format(
                                    getString(R.string.transactions_tax_sum),
                                    viewModel.year,
                                    it.taxSum
                                )*/
            }
        }

        binding.buttonTransSendEmail.setOnClickListener {
            if (requireActivity().checkStoragePermission()) {
                //fileName = viewModel.createLocalStatement()

                /*                if (fileName!!.exists()) {
                                    val uri = FileProvider.getUriForFile(
                                        requireContext(),
                                        requireContext().applicationContext.packageName + ".provider",
                                        fileName
                                    )
                                    val emailIntent = Intent(Intent.ACTION_SEND)
                                    emailIntent.type = "vnd.android.cursor.dir/email"
                                    emailIntent.putExtra(Intent.EXTRA_STREAM, uri)
                                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Расчёт налога от TaxApp")
                                    startActivity(Intent.createChooser(emailIntent, "Send email..."))*/

            } else binding.root.showSnackBar(R.string.transactions_error_send_report)
        }



        binding.buttonTransDeleteYear.setOnClickListener { navToTransactionDelete() }

        viewModel.attachToBaseFragment()
        currentStackSavedState.observeDelete()
        viewModel.observeState()
    }

    private fun TransactionsViewModel.observeState() =
        state.observe(viewLifecycleOwner) {
            when (it) {
                is BaseState.SuccessDelete -> popBackStack()
                else -> {}
            }
        }


    private fun SavedStateHandle.observeDelete() {
        getLiveData<Boolean>(TRANSACTIONS_DELETE_DIALOG_RESULT).observe(viewLifecycleOwner) {
            if (it) {
                viewModel.deleteTax()
            }
        }
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
            val buttonAdd: ImageButton = binding!!.buttonTransAdd
            buttonAdd.setOnClickListener { v -> findNavController(v).navigate(R.id.action_transactionsFragment_to_newTransactionFragment) }
            val buttonDelete: ImageButton = binding!!.buttonTransDeleteYear
            buttonDelete.setOnClickListener {
                val dialog = VerificationDialog()
                dialog.show(childFragmentManager, "deleteDialog")
                dialog.verificationStatus.observe(
                    viewLifecycleOwner,
                    object : Observer<Boolean?> {
                        override fun onChanged(status: Boolean) {
                            if (status) {
                                viewModel.deleteYear(year)
                            }
                        }
                    })
            }
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

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        if (fileName != null) {
            fileName!!.delete()
        }
        super.onResume()
    }

    private fun navToTransactionDelete() {
        findNavController().navigate(R.id.action_transactions_to_transactions_delete_dialog)
    }

    private fun navToTransactionDetail(transactionModel: TransactionModel) {
        activityViewModel.transaction = transactionModel
        findNavController().navigate(R.id.action_transactionsFragment_to_newTransactionFragment)
    }

    companion object {
        const val TRANSACTIONS_DELETE_DIALOG_RESULT = "transactions_delete_dialog_result"
    }
}